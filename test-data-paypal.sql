-- Script de prueba para PayPal
-- Inserta datos de ejemplo para probar la integración

-- NOTA: Este script asume que ya tienes un usuario en la base de datos
-- Si no, primero crea un usuario mediante el endpoint /api/auth/register

-- Ejemplo de insert de una orden de prueba
-- Reemplaza USER_ID con el ID de tu usuario

/*
INSERT INTO orders (user_id, total, subtotal, shipping_cost, discount, status, payment_method, created_at, updated_at)
VALUES (1, 99.99, 89.99, 10.00, 0.00, 'PENDIENTE', 'PayPal', NOW(), NOW());

-- Obtener el ID de la orden recién creada
-- SELECT LAST_INSERT_ID();

-- Insertar items de la orden
INSERT INTO order_items (order_id, product_id, name, price, quantity, image)
VALUES 
  (LAST_INSERT_ID(), 1, 'Producto de Prueba 1', 29.99, 2, 'https://via.placeholder.com/150'),
  (LAST_INSERT_ID(), 2, 'Producto de Prueba 2', 39.99, 1, 'https://via.placeholder.com/150');

-- Agregar información del cliente
UPDATE orders 
SET customer_name = 'Cliente de Prueba',
    customer_email = 'test@example.com',
    customer_phone = '+1234567890',
    customer_address_street = '123 Test Street',
    customer_address_city = 'Test City',
    customer_address_state = 'TS',
    customer_address_postal_code = '12345',
    customer_address_country = 'US'
WHERE id = LAST_INSERT_ID();
*/

-- Para ver las órdenes
-- SELECT * FROM orders WHERE payment_method = 'PayPal';

-- Para ver los detalles de una orden específica
-- SELECT * FROM orders WHERE id = 1;

-- Para ver los items de una orden
-- SELECT * FROM order_items WHERE order_id = 1;

-- Para verificar el estado del pago de PayPal
-- SELECT id, paypal_order_id, paypal_capture_id, paypal_payment_status, paypal_payer_email FROM orders WHERE paypal_order_id IS NOT NULL;

