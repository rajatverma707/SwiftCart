package com.rv.order.service;

import com.rv.order.dto.*;
import com.rv.order.dto.event.OrderEvent;
import com.rv.order.dto.event.OrderEventItem;
import com.rv.order.dto.event.OrderEventType;
import com.rv.order.dto.inventory.InventoryCheckResponse;
import com.rv.order.exception.OutOfStockException;
import com.rv.order.entity.OrderEntity;
import com.rv.order.entity.OrderItemsEntity;
import com.rv.order.entity.ShippingAddressEntity;
import com.rv.order.entity.UserEntity;
import com.rv.order.mapper.AddressMapper;
import com.rv.order.mapper.OrderItemMapper;
import com.rv.order.mapper.OrderMapper;
import com.rv.order.mapper.UserMapper;
import com.rv.order.repo.OrderItemRepo;
import com.rv.order.repo.OrderRepo;
import com.rv.order.repo.ShippingAddressRepo;
import com.rv.order.repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderItemRepo itemRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ShippingAddressRepo addressRepo;

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Override
    public PurchaseOrderResponseDto createOrder(PurchaseOrderRequestDto orderRequestDto) throws Exception {

        UserDto userDto = orderRequestDto.getUserDto();
        AddressDto addressDto = orderRequestDto.getAddressDto();
        OrderDto orderDto = orderRequestDto.getOrderDto();
        List<OrderItemDto> orderItemDtoList = orderRequestDto.getOrderItemDtoList();

        // Synchronous inventory check: if items are not available, do NOT create order
        InventoryCheckResponse inventoryResponse = inventoryClient.checkAvailability(orderItemDtoList);
        if (inventoryResponse == null || !inventoryResponse.isAvailable()) {
            String message = inventoryResponse != null && inventoryResponse.getMessage() != null
                ? inventoryResponse.getMessage()
                : "Some items are not available";

            // Publish an inventory failure event so notification-service can send failure email
            OrderEvent failureEvent = new OrderEvent();
            failureEvent.setEventType(OrderEventType.INVENTORY_RESERVATION_FAILED);
            failureEvent.setOrderId(null);
            failureEvent.setOrderTrackingNum(null);
            failureEvent.setCustomerEmail(userDto != null ? userDto.getEmail() : null);
            failureEvent.setEventTime(LocalDateTime.now());
            failureEvent.setItems(buildEventItemsFromDtos(orderItemDtoList));
            orderEventPublisher.publish(failureEvent);

            throw new OutOfStockException(message,
                inventoryResponse != null ? inventoryResponse.getUnavailableItems() : null);
        }

        // Saving Customer
        UserEntity userEntity = userRepo.findByEmail(userDto.getEmail());
        if (userEntity == null) {
            userEntity = UserMapper.convertToEntity(userDto);
            userRepo.save(userEntity);
            // TODO : Interservice communication
        }

        // Saving Address
        ShippingAddressEntity addressEntity = null;

        if (addressDto.getAddrId() == null) {
            addressEntity = AddressMapper.toEntity(addressDto);
            addressEntity.setUser(userEntity); // association mapping
            addressRepo.save(addressEntity);
        } else {
            addressEntity = addressRepo.findById(addressDto.getAddrId()).get();
        }

        // Saving   
        String orderTrackingNum = generateRandomTrackingNumber();
        orderDto.setOrderTrackingNum(orderTrackingNum);

        String razorpayOrderId = razorpayService.createRazorpayOrder(orderDto.getTotalPrice());
        orderDto.setRazorpayOrderId(razorpayOrderId);
        orderDto.setOrderStatus("CREATED");
        orderDto.setPaymentStatus("PENDING");

        OrderEntity orderEntity = OrderMapper.convertToEntity(orderDto);
        if (orderDto.getDeliveyDate() != null) {
            orderEntity.setDeliveryDate(orderDto.getDeliveyDate());
        } else {
            orderEntity.setDeliveryDate(calculateEstimatedDeliveryDate());
        }
        orderEntity.setUser(userEntity); // ASSOCIATION MAPPING
        orderEntity.setShippingAddress(addressEntity); // ASSOCIATION MAPPING

        orderEntity = orderRepo.save(orderEntity);

        // SAVE ORDER ITEMS
        for (OrderItemDto itemDto : orderItemDtoList) {
            OrderItemsEntity orderItemEntity = OrderItemMapper.convertToEntity(itemDto);
            orderItemEntity.setOrder(orderEntity); // Association Mapping
            itemRepo.save(orderItemEntity);
        }

        OrderEvent createdEvent = new OrderEvent();
        createdEvent.setEventType(OrderEventType.ORDER_CREATED);
        createdEvent.setOrderId(orderEntity.getOrderId());
        createdEvent.setOrderTrackingNum(orderEntity.getOrderTrackingNum());
        createdEvent.setCustomerEmail(userEntity.getEmail());
        createdEvent.setEventTime(LocalDateTime.now());
        createdEvent.setItems(buildEventItemsFromDtos(orderItemDtoList));
        orderEventPublisher.publish(createdEvent);

        // prepare final response
        PurchaseOrderResponseDto responseDto = new PurchaseOrderResponseDto();
        responseDto.setRazorpayOrderId(razorpayOrderId);
        responseDto.setOrderTrackingNumber(orderTrackingNum);
        responseDto.setOrderStatus("CREATED");
        responseDto.setPaymentStatus("PENDING");

        return responseDto;
    }

    @Override
    public PurchaseOrderResponseDto updateOrder(UpdateOrderRequestDto updateOrderRequestDto) {

        OrderEntity orderEntity = null;
        if (updateOrderRequestDto.getOrderTrackingNum() != null) {
            orderEntity = orderRepo.findByOrderTrackingNum(updateOrderRequestDto.getOrderTrackingNum());
        } else if (updateOrderRequestDto.getOrderId() != null) {
            orderEntity = orderRepo.findById(updateOrderRequestDto.getOrderId()).orElse(null);
        }

        if (orderEntity == null) {
            return null;
        }

        if (updateOrderRequestDto.getRazorpayPaymentId() != null) {
            orderEntity.setRazorpayPaymentId(updateOrderRequestDto.getRazorpayPaymentId());
        }
        orderEntity.setPaymentStatus(
                updateOrderRequestDto.getPaymentStatus() != null ? updateOrderRequestDto.getPaymentStatus() : "COMPLETED"
        );
        orderEntity.setOrderStatus(
                updateOrderRequestDto.getOrderStatus() != null ? updateOrderRequestDto.getOrderStatus() : "CONFIRMED"
        );
        if (orderEntity.getDeliveryDate() == null) {
            orderEntity.setDeliveryDate(calculateEstimatedDeliveryDate());
        }
        orderRepo.save(orderEntity);

        // prepare final response
        PurchaseOrderResponseDto responseDto = new PurchaseOrderResponseDto();
        responseDto.setRazorpayOrderId(orderEntity.getRazorpayOrderId());
        responseDto.setOrderTrackingNumber(orderEntity.getOrderTrackingNum());
        responseDto.setOrderStatus(orderEntity.getOrderStatus());
        responseDto.setPaymentStatus(orderEntity.getPaymentStatus());

        return responseDto;
    }

    @Override
    public PurchaseOrderResponseDto cancelOrder(String orderTrackingNumber) throws Exception {

        OrderEntity orderEntity = orderRepo.findByOrderTrackingNum(orderTrackingNumber);
        orderEntity.setOrderStatus("CANCELLED");
        orderEntity.setPaymentStatus("REFUND-IN-PROGRESS");
        orderEntity.setDeliveryDate(null);
        orderRepo.save(orderEntity);

        List<OrderItemsEntity> orderItems = itemRepo.findByOrderOrderId(orderEntity.getOrderId());
        OrderEvent cancelledEvent = new OrderEvent();
        cancelledEvent.setEventType(OrderEventType.ORDER_CANCELLED);
        cancelledEvent.setOrderId(orderEntity.getOrderId());
        cancelledEvent.setOrderTrackingNum(orderEntity.getOrderTrackingNum());
        cancelledEvent.setCustomerEmail(orderEntity.getUser() != null ? orderEntity.getUser().getEmail() : null);
        cancelledEvent.setEventTime(LocalDateTime.now());
        cancelledEvent.setItems(buildEventItemsFromEntities(orderItems));
        orderEventPublisher.publish(cancelledEvent);

        Integer totalPrice = orderEntity.getTotalPrice().intValue();

        razorpayService.refundPayment(orderEntity.getRazorpayPaymentId(), totalPrice * 100);

        // prepare final response
        PurchaseOrderResponseDto responseDto = new PurchaseOrderResponseDto();
        responseDto.setRazorpayOrderId(orderEntity.getRazorpayOrderId());
        responseDto.setOrderTrackingNumber(orderEntity.getOrderTrackingNum());
        responseDto.setOrderStatus(orderEntity.getOrderStatus());
        responseDto.setPaymentStatus("REFUND-IN-PROGRESS");
        return responseDto;
    }

    @Override
    public List<OrderDto> getCustomerOrders(String customerEmail) {
        List<OrderEntity> ordersList = orderRepo.findByUserEmail(customerEmail);
        return ordersList.stream().map(OrderMapper::convertToDto).toList();
    }


    private String generateRandomTrackingNumber() {

        // get the current timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        // generate random uuid
        String uuid = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        // combine OD with timestamp and UUID
        return "OD" + timestamp + uuid;
    }

    private LocalDateTime calculateEstimatedDeliveryDate() {
        return LocalDateTime.now().plusDays(7);
    }

    private List<OrderEventItem> buildEventItemsFromDtos(List<OrderItemDto> items) {
        return items.stream().map(itemDto -> {
            OrderEventItem eventItem = new OrderEventItem();
            eventItem.setProductId(itemDto.getProductId());
            eventItem.setQuantity(itemDto.getQuantity());
            return eventItem;
        }).collect(Collectors.toList());
    }

    private List<OrderEventItem> buildEventItemsFromEntities(List<OrderItemsEntity> items) {
        return items.stream().map(itemEntity -> {
            OrderEventItem eventItem = new OrderEventItem();
            eventItem.setProductId(itemEntity.getProductId());
            eventItem.setQuantity(itemEntity.getQuantity());
            return eventItem;
        }).collect(Collectors.toList());
    }
}

