# 💳 Integración PayPal - Huerto Hogar

## 🎯 Resumen

Este proyecto incluye una **integración completa de PayPal** para procesar pagos de forma segura. La implementación está lista para usar en modo **Sandbox** (gratuito) para desarrollo y pruebas.

## ✨ Características

- ✅ Creación de órdenes de pago en PayPal
- ✅ Redirección segura a PayPal para aprobación
- ✅ Captura automática de pagos
- ✅ Almacenamiento de información de transacciones
- ✅ Manejo de estados de pago
- ✅ API REST completa para integración frontend
- ✅ Documentación completa con Swagger
- ✅ Testing con cuentas Sandbox

## 📚 Documentación

| Documento | Descripción |
|-----------|-------------|
| [QUICK_START.md](QUICK_START.md) | ⚡ Empezar en 5 minutos |
| [PAYPAL_INTEGRATION.md](PAYPAL_INTEGRATION.md) | 📖 Guía completa de integración |
| [FRONTEND_EXAMPLE.md](FRONTEND_EXAMPLE.md) | 💻 Ejemplos de código React/JavaScript |
| [paypal-test-postman.json](paypal-test-postman.json) | 📮 Colección de Postman para testing |

## 🚀 Inicio Rápido

### 1. Configurar Credenciales

Edita `src/main/resources/application.properties`:

```properties
paypal.mode=sandbox
paypal.client-id=TU_CLIENT_ID
paypal.client-secret=TU_SECRET
```

### 2. Ejecutar el Proyecto

```bash
./mvnw spring-boot:run
```

### 3. Probar con Swagger

Abre: http://localhost:8080/swagger-ui.html

Busca la sección **"PayPal"** y prueba los endpoints.

## 🔌 Endpoints de la API

### Crear Orden de PayPal
```
POST /api/paypal/create-order
Authorization: Bearer {token}
Content-Type: application/json

{
  "orderId": 1,
  "amount": 99.99,
  "currency": "USD"
}
```

**Respuesta:**
```json
{
  "paypalOrderId": "8RS12345ABCD",
  "status": "CREATED",
  "approvalUrl": "https://sandbox.paypal.com/checkoutnow?token=...",
  "orderId": 1
}
```

### Capturar Pago
```
POST /api/paypal/capture-order/{paypalOrderId}
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "paypalOrderId": "8RS12345ABCD",
  "captureId": "9TH67890EFGH",
  "status": "COMPLETED",
  "amount": 99.99,
  "currency": "USD",
  "orderId": 1,
  "payerEmail": "buyer@example.com",
  "payerName": "John Doe"
}
```

### Obtener Detalles de Orden
```
GET /api/paypal/order/{paypalOrderId}
Authorization: Bearer {token}
```

## 🔄 Flujo de Pago

```
1. Cliente crea orden          → POST /api/orders
2. Sistema crea orden PayPal   → POST /api/paypal/create-order
3. Cliente aprueba en PayPal   → Redirigido a approvalUrl
4. Sistema captura pago        → POST /api/paypal/capture-order/{id}
5. Orden actualizada           → Status = PROCESANDO
```

## 🏗️ Arquitectura

```
Frontend (React)
    ↓
PayPalController
    ↓
PayPalService
    ↓
PayPal SDK ←→ PayPal API (Sandbox/Live)
    ↓
OrderRepository
    ↓
Base de Datos (H2/MySQL)
```

## 📦 Dependencias Agregadas

```xml
<dependency>
    <groupId>com.paypal.sdk</groupId>
    <artifactId>checkout-sdk</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 🗂️ Archivos Creados

### Backend
```
src/main/java/com/huerto/
├── config/
│   └── PayPalConfig.java                 ← Configuración del SDK
├── controller/
│   └── PayPalController.java            ← Endpoints REST
├── service/
│   └── PayPalService.java               ← Lógica de negocio
└── dto/
    ├── PayPalOrderRequest.java          ← Request para crear orden
    ├── PayPalOrderResponse.java         ← Response con approval URL
    └── PayPalCaptureResponse.java       ← Response de captura
```

### Modelo Actualizado
```
model/Order.java
├── paypalOrderId       ← ID de la orden en PayPal
├── paypalCaptureId     ← ID de la captura
├── paypalPayerEmail    ← Email del pagador
├── paypalPayerName     ← Nombre del pagador
└── paypalPaymentStatus ← Estado del pago
```

### Documentación
```
Huerto-Hogar_backend/
├── PAYPAL_INTEGRATION.md      ← Documentación completa
├── FRONTEND_EXAMPLE.md        ← Ejemplos de frontend
├── QUICK_START.md             ← Guía rápida
├── paypal-test-postman.json   ← Colección Postman
└── test-data-paypal.sql       ← Datos de prueba
```

## 🧪 Testing

### Opción 1: Postman
1. Importa `paypal-test-postman.json` en Postman
2. Ejecuta los requests en orden
3. Sigue las instrucciones en la consola

### Opción 2: Swagger UI
1. Abre http://localhost:8080/swagger-ui.html
2. Autentícate con `/api/auth/login`
3. Copia el token
4. Haz clic en "Authorize" y pega el token
5. Prueba los endpoints de PayPal

### Opción 3: cURL
```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}' \
  | jq -r '.token')

# Crear orden PayPal
RESPONSE=$(curl -s -X POST http://localhost:8080/api/paypal/create-order \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"amount":99.99,"currency":"USD"}')

# Extraer approval URL
echo $RESPONSE | jq -r '.approvalUrl'
```

## 🔐 Seguridad

- ✅ Autenticación JWT requerida para todos los endpoints
- ✅ Validación de datos en el backend
- ✅ Credenciales nunca expuestas al frontend
- ✅ Comunicación HTTPS con PayPal
- ✅ Verificación de pagos directamente con PayPal

## 🌍 Entornos

### Sandbox (Desarrollo)
```properties
paypal.mode=sandbox
# Usa credenciales de developer.paypal.com
```

### Live (Producción)
```properties
paypal.mode=live
# Usa credenciales de tu cuenta de negocio real
```

## 💰 Costos

- **Sandbox**: Completamente GRATIS
- **Producción**: PayPal cobra una comisión por transacción
  - ~2.9% + $0.30 USD por transacción (varía según país)
  - Consulta las tarifas oficiales en paypal.com

## 📊 Monitoreo

### Ver transacciones en PayPal
1. Dashboard: https://developer.paypal.com/dashboard/
2. Ve a "Sandbox" → "Accounts"
3. Selecciona tu cuenta de negocio
4. Haz clic en "View details"

### Ver en la base de datos
```sql
SELECT 
  id,
  paypal_order_id,
  paypal_capture_id,
  paypal_payment_status,
  paypal_payer_email,
  total,
  created_at
FROM orders
WHERE paypal_order_id IS NOT NULL
ORDER BY created_at DESC;
```

## 🐛 Solución de Problemas

| Error | Solución |
|-------|----------|
| `Invalid credentials` | Verifica Client ID y Secret en properties |
| `Order not found` | Crea primero una orden con `/api/orders` |
| `Authentication required` | Incluye el token JWT en el header |
| `CORS error` | Verifica la configuración de CORS en SecurityConfig |

## 🎓 Recursos

- 📖 [PayPal Developer Docs](https://developer.paypal.com/docs/)
- 🔧 [PayPal Sandbox](https://developer.paypal.com/dashboard/)
- 💻 [Checkout SDK Java](https://github.com/paypal/Checkout-Java-SDK)
- 🎥 [PayPal Integration Tutorial](https://developer.paypal.com/video/)

## 📝 Notas Importantes

1. **Nunca commits credenciales reales** al repositorio
2. **Usa variables de entorno** en producción
3. **Prueba exhaustivamente** en Sandbox antes de ir a producción
4. **Configura webhooks** para notificaciones asíncronas (opcional)
5. **Maneja reembolsos** y disputas según tu política de negocio

## ✅ Checklist de Producción

Antes de ir a producción, asegúrate de:

- [ ] Crear cuenta de negocio verificada en PayPal
- [ ] Obtener credenciales Live
- [ ] Cambiar `paypal.mode=live`
- [ ] Configurar variables de entorno
- [ ] Probar flujo completo en producción
- [ ] Configurar webhooks (opcional)
- [ ] Implementar manejo de errores robusto
- [ ] Configurar logs de transacciones
- [ ] Definir política de reembolsos
- [ ] Preparar soporte al cliente

## 🤝 Soporte

¿Necesitas ayuda?

1. Revisa la documentación completa en `PAYPAL_INTEGRATION.md`
2. Consulta los ejemplos en `FRONTEND_EXAMPLE.md`
3. Verifica el troubleshooting en la documentación
4. Contacta a PayPal Developer Support

---

**¡Listo para aceptar pagos!** 🚀💳

Para empezar rápidamente, ve a [QUICK_START.md](QUICK_START.md)

