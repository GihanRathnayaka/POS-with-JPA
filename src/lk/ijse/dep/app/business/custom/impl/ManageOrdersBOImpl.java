package lk.ijse.dep.app.business.custom.impl;

import lk.ijse.dep.app.business.Converter;
import lk.ijse.dep.app.business.custom.ManageOrdersBO;
import lk.ijse.dep.app.dao.DAOFactory;
import lk.ijse.dep.app.dao.custom.*;
import lk.ijse.dep.app.dto.*;
import lk.ijse.dep.app.entity.*;
import lk.ijse.dep.app.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManageOrdersBOImpl implements ManageOrdersBO {

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private ItemDAO itemDAO;
    private QueryDAO queryDAO;
    private CustomerDAO customer;

    public ManageOrdersBOImpl() {
        orderDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER);
        orderDetailDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER_DETAIL);
        itemDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ITEM);
        queryDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.QUERY);
        customer= DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    }



    @Override
    public List<OrderDTO2> getOrdersWithCustomerNamesAndTotals() throws Exception {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        queryDAO.setEntityManager(em);
        return queryDAO.findAllOrdersWithCustomerNameAndTotal().map(ce -> {
            return Converter.getDTOList(ce, OrderDTO2.class);
        }).get();

    }

    @Override
    public List<OrderDTO> getOrders() throws Exception {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            orderDAO.setEntityManager(em);
            em.getTransaction().begin();
            List<OrderDTO> itemDAOS = orderDAO.findAll().map(Converter::<OrderDTO>getDTOList).get();
            em.getTransaction().commit();
            return itemDAOS;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        }

    }

    @Override
    public String generateOrderId() throws Exception {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            queryDAO.setEntityManager(em);
            em.getTransaction().begin();
            int count = queryDAO.count();
            em.getTransaction().commit();
            return "O00"+(++count);
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }

    @Override
    public void createOrder(OrderDTO dto) throws Exception {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            orderDAO.setEntityManager(em);
            em.getTransaction().begin();
            customer.setEntityManager(em);
            CustomerDTO customerDTO = customer.find(dto.getCustomerId()).map(Converter::<CustomerDTO>getDTO).orElse(null);
            Customer entity = Converter.getEntity(customerDTO);
            Order e = Converter.getEntity(dto);
            e.setCustomer(entity);
            orderDAO.save(e);

            for (OrderDetailDTO od: dto.getOrderDetailDTOS()) {
                orderDetailDAO.setEntityManager(em);
                OrderDetail en = Converter.getEntity(od);
                en.setOrderDetailPK(new OrderDetailPK(e.getId(),od.getCode()));
                orderDetailDAO.save(en);
                itemDAO.setEntityManager(em);
                Optional<Item> item = itemDAO.find(od.getCode());
                Item i = item.get();
                i.setQtyOnHand(i.getQtyOnHand()-od.getQty());
            }

            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        }

    }

    @Override
    public OrderDTO findOrder(String orderId) throws Exception {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            orderDAO.setEntityManager(em);
            orderDAO.setEntityManager(em);
            em.getTransaction().begin();
            OrderDTO orderDTO = orderDAO.find(orderId).map(Converter::<OrderDTO>getDTO).orElse(null);
            orderDetailDAO.setEntityManager(em);
            List<OrderDetail> entities = orderDetailDAO.findAll().get();
            List<OrderDetailDTO> list = new ArrayList<>();
            entities.forEach(c -> {
                if(orderId.equals(c.getOrderDetailPK().getOrderId())){
                    list.add( new OrderDetailDTO(c.getOrderDetailPK().getItemCode(),c.getItemCode().getDescription(),c.getQty(),c.getUnitPrice()));
                }
            });
            em.getTransaction().commit();
            orderDTO.setOrderDetailDTOS(list);
            return orderDTO;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        }



    }

//    private OrderDAO orderDAO;
//    private OrderDetailDAO orderDetailDAO;
//    private ItemDAO itemDAO;
//    private QueryDAO queryDAO;
//
//    public ManageOrdersBOImpl() {
//        orderDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER);
//        orderDetailDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER_DETAIL);
//        itemDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ITEM);
//        queryDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.QUERY);
//    }
//
//    public List<OrderDTO2> getOrdersWithCustomerNamesAndTotals() throws Exception {
//
//        return queryDAO.findAllOrdersWithCustomerNameAndTotal().map(ce -> {
//            return Converter.getDTOList(ce, OrderDTO2.class);
//        }).get();
//
//    }
//
//    public List<OrderDTO> getOrders() throws Exception {
//
//        List<Order> orders = orderDAO.findAll().get();
//        ArrayList<OrderDTO> tmpDTOs = new ArrayList<>();
//
//        for (Order order : orders) {
//            List<OrderDetailDTO> tmpOrderDetailsDtos = queryDAO.findOrderDetailsWithItemDescriptions(order.getId()).map(ce -> {
//                return Converter.getDTOList(ce, OrderDetailDTO.class);
//            }).get();
//
//            OrderDTO dto = new OrderDTO(order.getId(),
//                    order.getDate().toLocalDate(),
//                    order.getCustomerId(), tmpOrderDetailsDtos);
//            tmpDTOs.add(dto);
//        }
//
//        return tmpDTOs;
//    }
//
//    public String generateOrderId() throws Exception {
//        return orderDAO.count() + 1 + "";
//    }
//
//    public void createOrder(OrderDTO dto) throws Exception {
//
//        DBConnection.getConnection().setAutoCommit(false);
//
//        try {
//
//            boolean result = orderDAO.save(new Order(dto.getId(), Date.valueOf(dto.getDate()), dto.getCustomerId()));
//
//            if (!result) {
//                return;
//            }
//
//            for (OrderDetailDTO detailDTO : dto.getOrderDetailDTOS()) {
//                result = orderDetailDAO.save(new OrderDetail(dto.getId(),
//                        detailDTO.getCode(), detailDTO.getQty(), detailDTO.getUnitPrice()));
//
//                if (!result) {
//                    DBConnection.getConnection().rollback();
//                    return;
//                }
//
//                Item item = itemDAO.find(detailDTO.getCode()).get();
//                int qty = item.getQtyOnHand() - detailDTO.getQty();
//                item.setQtyOnHand(qty);
//                itemDAO.update(item);
//
//            }
//
//            DBConnection.getConnection().commit();
//
//        } catch (Exception ex) {
//            DBConnection.getConnection().rollback();
//            ex.printStackTrace();
//        } finally {
//            DBConnection.getConnection().setAutoCommit(true);
//        }
//
//    }
//
//    public OrderDTO findOrder(String orderId) throws Exception {
//        Order order = orderDAO.find(orderId).get();
//
//        List<OrderDetailDTO> tmpOrderDetailsDtos = queryDAO.findOrderDetailsWithItemDescriptions(order.getId()).map(ce -> {
//            return Converter.getDTOList(ce, OrderDetailDTO.class);
//        }).get();
//
//        return new OrderDTO(order.getId(), order.getDate().toLocalDate(), order.getCustomerId(), tmpOrderDetailsDtos);
//    }
}
