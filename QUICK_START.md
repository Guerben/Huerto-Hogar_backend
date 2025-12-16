# 🚀 Quick Start - Integración PayPal

Guía rápida para empezar a usar PayPal en menos de 5 minutos.

## 📋 Requisitos Previos

- Java 17+
- Maven
- Cuenta de desarrollador de PayPal (gratuita)

## ⚡ Pasos Rápidos

### 1. Obtener Credenciales de PayPal (2 minutos)

1. Ve a: https://developer.paypal.com/dashboard/
2. Inicia sesión o crea una cuenta
3. Ve a **"My Apps & Credentials"**
4. En la sección **"Sandbox"**, haz clic en **"Create App"**
5. Copia el **Client ID** y **Secret**

### 2. Configurar Backend (1 minuto)

Edita `src/main/resources/application.properties`:

```properties
# Reemplaza con tus credenciales
paypal.client-id=TU_CLIENT_ID_AQUI
paypal.client-secret=TU_SECRET_AQUI
```

### 3. Ejecutar Backend (30 segundos)

```bash
cd Huerto-Hogar_backend
./mvnw spring-boot:run
```

O en Windows:
```bash
mvnw.cmd spring-boot:run
```

### 4. Probar la API (1 minuto)

#### Opción A: Usar Swagger UI
1. Abre: http://localhost:8080/swagger-ui.html
2. Busca el endpoint `/api/paypal/create-order`
3. Prueba con este JSON:

```json
{
  "orderId": 1,
  "amount": 10.00,
  "currency": "USD"
}
```

#### Opción B: Usar curl

```bash
# 1. Login para obtener token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tu@email.com",
    "password": "tupassword"
  }'

# 2. Crear orden PayPal (usa el token del paso anterior)
curl -X POST http://localhost:8080/api/paypal/create-order \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{
    "orderId": 1,
    "amount": 10.00,
    "currency": "USD"
  }'
```

#### Opción C: Usar Postman

1. **Login**:
   - POST: `http://localhost:8080/api/auth/login`
   - Body (JSON):
     ```json
     {
       "email": "tu@email.com",
       "password": "tupassword"
     }
     ```
   - Copia el `token` de la respuesta

2. **Crear Orden PayPal**:
   - POST: `http://localhost:8080/api/paypal/create-order`
   - Headers:
     - `Authorization`: `Bearer TU_TOKEN`
     - `Content-Type`: `application/json`
   - Body (JSON):
     ```json
     {
       "orderId": 1,
       "amount": 10.00,
       "currency": "USD"
     }
     ```

3. La respuesta incluirá un `approvalUrl`. Ábrelo en el navegador.

4. **Capturar Pago**:
   - Después de aprobar en PayPal, copia el `token` de la URL
   - POST: `http://localhost:8080/api/paypal/capture-order/{token}`
   - Headers:
     - `Authorization`: `Bearer TU_TOKEN`

### 5. Aprobar Pago en PayPal (30 segundos)

1. Abre la URL `approvalUrl` que recibiste
2. Inicia sesión con una cuenta sandbox de PayPal
3. Aprueba el pago

**Cuentas de prueba**: Ve a Dashboard > Sandbox > Accounts para ver las credenciales.

### 6. Capturar el Pago

Después de aprobar, copia el `token` de la URL y ejecuta:

```bash
curl -X POST http://localhost:8080/api/paypal/capture-order/TOKEN_DE_PAYPAL \
  -H "Authorization: Bearer TU_TOKEN_DE_AUTH"
```

## 🎉 ¡Listo!

Has completado un pago con PayPal. Ahora puedes:

- ✅ Crear órdenes de pago
- ✅ Redirigir usuarios a PayPal
- ✅ Capturar pagos aprobados
- ✅ Ver detalles de transacciones

## 📚 Siguientes Pasos

1. **Integrar en Frontend**: Lee `FRONTEND_EXAMPLE.md`
2. **Documentación Completa**: Lee `PAYPAL_INTEGRATION.md`
3. **Producción**: Configura credenciales Live

## 🔍 Verificar Integración

### Endpoints Disponibles:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/paypal/create-order` | Crear orden |
| POST | `/api/paypal/capture-order/{id}` | Capturar pago |
| GET | `/api/paypal/order/{id}` | Ver detalles |

### Estados del Pago:

- `CREATED`: Orden creada, esperando aprobación
- `APPROVED`: Usuario aprobó, listo para capturar
- `COMPLETED`: Pago capturado exitosamente

## ❓ Problemas Comunes

### "Invalid client credentials"
- Verifica que copiaste bien el Client ID y Secret
- Asegúrate de usar credenciales de Sandbox

### "Order not found"
- Crea primero una orden con POST `/api/orders`
- Usa el ID de esa orden en el request de PayPal

### "Authentication required"
- Necesitas un token JWT válido
- Login con POST `/api/auth/login` primero

## 🛠 Herramientas Útiles

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **PayPal Dashboard**: https://developer.paypal.com/dashboard/
- **Sandbox Accounts**: https://developer.paypal.com/dashboard/accounts

## 💡 Tips

1. **Usa Postman**: Facilita las pruebas
2. **Guarda colecciones**: Para reutilizar requests
3. **Variables de entorno**: Para cambiar entre dev/prod
4. **Logs**: Revisa la consola del backend para debug

## 📞 Ayuda

Si algo no funciona:

1. Revisa los logs del backend
2. Verifica las credenciales en `application.properties`
3. Consulta `PAYPAL_INTEGRATION.md` para más detalles
4. Revisa la documentación oficial de PayPal

---

**¿Todo funcionó?** ¡Genial! Ahora puedes integrar PayPal en tu frontend. 🚀

