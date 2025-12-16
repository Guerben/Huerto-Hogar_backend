# Integración de PayPal - Guía Completa

## 📋 Descripción

Este proyecto incluye una integración completa con PayPal para procesar pagos de manera segura. La implementación utiliza el SDK oficial de PayPal en modo Sandbox (gratuito) para desarrollo y testing.

## 🚀 Configuración Inicial

### 1. Crear una Cuenta de Desarrollador en PayPal

1. Visita [PayPal Developer Dashboard](https://developer.paypal.com/dashboard/)
2. Inicia sesión o crea una cuenta de desarrollador
3. Accede a "Dashboard"

### 2. Crear una Aplicación Sandbox

1. En el Dashboard, ve a **"My Apps & Credentials"**
2. En la sección **"Sandbox"**, haz clic en **"Create App"**
3. Dale un nombre a tu aplicación (ej: "Huerto Hogar Sandbox")
4. Selecciona una cuenta de negocio sandbox o crea una nueva
5. Haz clic en **"Create App"**

### 3. Obtener Credenciales

Una vez creada la aplicación, verás dos valores importantes:

- **Client ID**: Identificador público de tu aplicación
- **Secret**: Clave secreta (mantén esto seguro)

### 4. Configurar el Backend

Edita el archivo `src/main/resources/application.properties`:

```properties
# PayPal Configuration (Sandbox)
paypal.mode=sandbox
paypal.client-id=TU_CLIENT_ID_AQUI
paypal.client-secret=TU_SECRET_AQUI
paypal.return-url=http://localhost:3000/payment/success
paypal.cancel-url=http://localhost:3000/payment/cancel
```

**⚠️ IMPORTANTE**: 
- Para **producción**, cambia `paypal.mode=live` y usa las credenciales de Live
- **NUNCA** commits las credenciales reales al repositorio
- Usa variables de entorno en producción

## 📡 API Endpoints

### 1. Crear Orden de PayPal

**POST** `/api/paypal/create-order`

Crea una orden de pago en PayPal y retorna la URL de aprobación.

**Request Body:**
```json
{
  "orderId": 1,
  "amount": 99.99,
  "currency": "USD",
  "returnUrl": "http://localhost:3000/payment/success",
  "cancelUrl": "http://localhost:3000/payment/cancel"
}
```

**Response:**
```json
{
  "paypalOrderId": "8RS12345ABCD",
  "status": "CREATED",
  "approvalUrl": "https://www.sandbox.paypal.com/checkoutnow?token=8RS12345ABCD",
  "orderId": 1
}
```

### 2. Capturar Pago

**POST** `/api/paypal/capture-order/{paypalOrderId}`

Captura el pago después de que el usuario apruebe la transacción.

**Response:**
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

### 3. Obtener Detalles de Orden

**GET** `/api/paypal/order/{paypalOrderId}`

Obtiene los detalles completos de una orden de PayPal.

## 🔄 Flujo de Pago

### Flujo del Usuario

1. **Cliente crea una orden** → POST `/api/orders`
2. **Sistema crea orden PayPal** → POST `/api/paypal/create-order`
3. **Cliente es redirigido** → a `approvalUrl` de PayPal
4. **Cliente aprueba el pago** → en el sitio de PayPal
5. **PayPal redirige** → a `returnUrl` con el `token` en la query
6. **Frontend captura el pago** → POST `/api/paypal/capture-order/{token}`
7. **Sistema confirma pago** → Actualiza orden a `PROCESANDO`

### Diagrama de Secuencia

```
Frontend          Backend          PayPal
   |                 |                |
   |-- Create Order->|                |
   |                 |                |
   |<- Order ID -----|                |
   |                 |                |
   |-- PayPal Req -->|                |
   |                 |-- Create ----->|
   |                 |<-- Token ------|
   |<- Approval URL--|                |
   |                 |                |
   |-------- Redirect to PayPal ----->|
   |                                  |
   |<------- User Approves ---------->|
   |                                  |
   |<-- Redirect with Token ----------|
   |                 |                |
   |-- Capture ----->|                |
   |                 |-- Capture ---->|
   |                 |<-- Confirm ----|
   |<- Success ------|                |
```

## 🧪 Testing con Sandbox

### Cuentas de Prueba

PayPal crea automáticamente cuentas de prueba. Puedes verlas en:
**Dashboard → Sandbox → Accounts**

Tipos de cuentas:
- **Personal/Buyer**: Para simular compradores
- **Business**: Para recibir pagos

### Credenciales de Prueba

Para probar pagos, usa las credenciales de las cuentas sandbox:
- Email: `sb-xxxxx@personal.example.com`
- Password: (generada automáticamente, visible en el dashboard)

### Tarjetas de Prueba

PayPal también permite usar tarjetas de prueba. Ejemplos:

| Tipo | Número | CVV | Fecha |
|------|--------|-----|-------|
| Visa | 4032 0372 6025 1462 | 123 | Cualquier fecha futura |
| MasterCard | 5425 2334 2443 4010 | 123 | Cualquier fecha futura |

## 💻 Ejemplo de Integración Frontend (React)

```javascript
// 1. Crear orden en tu backend
const createOrder = async (orderId) => {
  const response = await fetch('/api/paypal/create-order', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      orderId: orderId,
      amount: 99.99,
      currency: 'USD'
    })
  });
  
  const data = await response.json();
  
  // 2. Redirigir al usuario a PayPal
  window.location.href = data.approvalUrl;
};

// 3. En la página de éxito, capturar el pago
const capturePayment = async () => {
  // Obtener el token de la URL
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get('token');
  
  const response = await fetch(`/api/paypal/capture-order/${token}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const result = await response.json();
  
  if (result.status === 'COMPLETED') {
    // Pago exitoso
    console.log('Pago completado:', result);
  }
};
```

## 🔐 Seguridad

### Buenas Prácticas

1. **Nunca expongas las credenciales**
   - Usa variables de entorno
   - No las commits al repositorio
   - Usa diferentes credenciales para dev/prod

2. **Valida en el backend**
   - Nunca confíes en datos del frontend
   - Verifica montos en el servidor
   - Valida el estado del pago con PayPal

3. **Manejo de errores**
   - Implementa reintentos para errores de red
   - Registra todas las transacciones
   - Maneja cancelaciones del usuario

4. **Webhooks (opcional)**
   - Configura webhooks para notificaciones asíncronas
   - Valida la firma de los webhooks
   - Maneja eventos de reembolsos y disputas

## 🌐 Producción

### Pasos para ir a Producción

1. **Verifica tu cuenta de negocio**
   - En PayPal.com (no en developer.paypal.com)
   - Completa toda la información de negocio
   - Verifica tu cuenta bancaria

2. **Crea aplicación en Live**
   - Dashboard → Live → Create App
   - Obtén credenciales de producción

3. **Actualiza configuración**
   ```properties
   paypal.mode=live
   paypal.client-id=LIVE_CLIENT_ID
   paypal.client-secret=LIVE_SECRET
   ```

4. **Testing en producción**
   - Usa tarjetas reales con cantidades pequeñas
   - Verifica el flujo completo
   - Haz reembolsos de prueba

## 📊 Campos en la Base de Datos

El modelo `Order` incluye los siguientes campos relacionados con PayPal:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `paypalOrderId` | String | ID de la orden en PayPal |
| `paypalCaptureId` | String | ID de la captura del pago |
| `paypalPayerEmail` | String | Email del pagador |
| `paypalPayerName` | String | Nombre del pagador |
| `paypalPaymentStatus` | String | Estado del pago (CREATED, APPROVED, COMPLETED) |

## 🐛 Troubleshooting

### Errores Comunes

1. **"Invalid client credentials"**
   - Verifica que el Client ID y Secret sean correctos
   - Asegúrate de usar credenciales del mismo ambiente (sandbox/live)

2. **"Order already captured"**
   - No puedes capturar una orden dos veces
   - Verifica el estado antes de capturar

3. **CORS errors**
   - Verifica la configuración de CORS en SecurityConfig
   - Asegúrate que las URLs coinciden

4. **Redirect no funciona**
   - Verifica las URLs de retorno y cancelación
   - Deben ser URLs válidas y accesibles

## 📚 Recursos Adicionales

- [PayPal Developer Documentation](https://developer.paypal.com/docs/)
- [PayPal Checkout Integration](https://developer.paypal.com/docs/checkout/)
- [PayPal SDK for Java](https://github.com/paypal/Checkout-Java-SDK)
- [PayPal Sandbox Testing](https://developer.paypal.com/docs/api-basics/sandbox/)

## 💡 Notas Adicionales

- **Monedas soportadas**: USD, EUR, GBP, y muchas más
- **Comisiones**: PayPal cobra comisiones por transacción (consulta su sitio)
- **Límites**: Las cuentas sandbox tienen límites más bajos que las de producción
- **Soporte**: PayPal tiene soporte técnico para desarrolladores

---

**¿Necesitas ayuda?** Consulta la documentación oficial de PayPal o contacta su soporte técnico.

