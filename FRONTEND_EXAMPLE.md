# Ejemplo de Integración Frontend con PayPal

## 🎨 Componente React - Checkout con PayPal

Este documento muestra cómo integrar PayPal en tu aplicación React/Next.js.

## 📦 Instalación (Opcional)

Puedes usar el SDK de PayPal en el frontend para una mejor experiencia:

```bash
npm install @paypal/react-paypal-js
```

## 🔧 Opción 1: Integración Simple (Redirect)

### Componente de Checkout

```jsx
// components/PayPalCheckout.jsx
import { useState } from 'react';
import axios from 'axios';

const PayPalCheckout = ({ orderId, amount }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handlePayPalPayment = async () => {
    try {
      setLoading(true);
      setError(null);

      // Obtener token del localStorage o contexto
      const token = localStorage.getItem('authToken');

      // Crear orden de PayPal
      const response = await axios.post(
        'http://localhost:8080/api/paypal/create-order',
        {
          orderId: orderId,
          amount: amount,
          currency: 'USD'
        },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      // Redirigir a PayPal
      window.location.href = response.data.approvalUrl;
      
    } catch (err) {
      console.error('Error al iniciar pago:', err);
      setError('Error al procesar el pago. Por favor, intenta nuevamente.');
      setLoading(false);
    }
  };

  return (
    <div className="paypal-checkout">
      <button
        onClick={handlePayPalPayment}
        disabled={loading}
        className="paypal-button"
      >
        {loading ? 'Procesando...' : 'Pagar con PayPal'}
      </button>
      
      {error && (
        <div className="error-message">
          {error}
        </div>
      )}
    </div>
  );
};

export default PayPalCheckout;
```

### Página de Éxito

```jsx
// pages/PaymentSuccess.jsx
import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const PaymentSuccess = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('processing');
  const [paymentDetails, setPaymentDetails] = useState(null);

  useEffect(() => {
    const capturePayment = async () => {
      // Obtener el token de PayPal de la URL
      const paypalToken = searchParams.get('token');
      
      if (!paypalToken) {
        setStatus('error');
        return;
      }

      try {
        const authToken = localStorage.getItem('authToken');
        
        // Capturar el pago
        const response = await axios.post(
          `http://localhost:8080/api/paypal/capture-order/${paypalToken}`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${authToken}`
            }
          }
        );

        if (response.data.status === 'COMPLETED') {
          setStatus('success');
          setPaymentDetails(response.data);
          
          // Redirigir a la orden después de 3 segundos
          setTimeout(() => {
            navigate(`/orders/${response.data.orderId}`);
          }, 3000);
        } else {
          setStatus('error');
        }
        
      } catch (err) {
        console.error('Error al capturar pago:', err);
        setStatus('error');
      }
    };

    capturePayment();
  }, [searchParams, navigate]);

  if (status === 'processing') {
    return (
      <div className="payment-processing">
        <h2>Procesando tu pago...</h2>
        <div className="spinner"></div>
      </div>
    );
  }

  if (status === 'success') {
    return (
      <div className="payment-success">
        <h2>✅ ¡Pago Exitoso!</h2>
        <p>Tu pago ha sido procesado correctamente.</p>
        {paymentDetails && (
          <div className="payment-details">
            <p><strong>Monto:</strong> ${paymentDetails.amount} {paymentDetails.currency}</p>
            <p><strong>ID de Transacción:</strong> {paymentDetails.captureId}</p>
            <p><strong>Email:</strong> {paymentDetails.payerEmail}</p>
          </div>
        )}
        <p>Redirigiendo a tu orden...</p>
      </div>
    );
  }

  return (
    <div className="payment-error">
      <h2>❌ Error en el Pago</h2>
      <p>Hubo un problema al procesar tu pago.</p>
      <button onClick={() => navigate('/cart')}>
        Volver al Carrito
      </button>
    </div>
  );
};

export default PaymentSuccess;
```

### Página de Cancelación

```jsx
// pages/PaymentCancel.jsx
import { useNavigate } from 'react-router-dom';

const PaymentCancel = () => {
  const navigate = useNavigate();

  return (
    <div className="payment-cancel">
      <h2>❌ Pago Cancelado</h2>
      <p>Has cancelado el proceso de pago.</p>
      <button onClick={() => navigate('/cart')}>
        Volver al Carrito
      </button>
    </div>
  );
};

export default PaymentCancel;
```

## 🎨 Opción 2: Integración Avanzada (Con SDK de PayPal)

### Setup del Provider

```jsx
// App.jsx o _app.js
import { PayPalScriptProvider } from "@paypal/react-paypal-js";

const initialOptions = {
  "client-id": "YOUR_PAYPAL_CLIENT_ID",
  currency: "USD",
  intent: "capture",
};

function App() {
  return (
    <PayPalScriptProvider options={initialOptions}>
      {/* Tu aplicación */}
    </PayPalScriptProvider>
  );
}
```

### Componente con Botones de PayPal

```jsx
// components/PayPalButtons.jsx
import { PayPalButtons } from "@paypal/react-paypal-js";
import axios from 'axios';

const PayPalCheckoutButtons = ({ orderId, amount }) => {
  const createOrder = async () => {
    try {
      const token = localStorage.getItem('authToken');
      
      const response = await axios.post(
        'http://localhost:8080/api/paypal/create-order',
        {
          orderId: orderId,
          amount: amount,
          currency: 'USD'
        },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      return response.data.paypalOrderId;
      
    } catch (error) {
      console.error('Error creating order:', error);
      throw error;
    }
  };

  const onApprove = async (data) => {
    try {
      const token = localStorage.getItem('authToken');
      
      const response = await axios.post(
        `http://localhost:8080/api/paypal/capture-order/${data.orderID}`,
        {},
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );

      if (response.data.status === 'COMPLETED') {
        alert('Pago completado exitosamente!');
        // Redirigir o actualizar UI
        window.location.href = `/orders/${response.data.orderId}`;
      }
      
    } catch (error) {
      console.error('Error capturing order:', error);
      alert('Error al procesar el pago');
    }
  };

  const onError = (err) => {
    console.error('PayPal Error:', err);
    alert('Ocurrió un error con PayPal');
  };

  return (
    <PayPalButtons
      createOrder={createOrder}
      onApprove={onApprove}
      onError={onError}
      style={{
        layout: 'vertical',
        color: 'gold',
        shape: 'rect',
        label: 'paypal'
      }}
    />
  );
};

export default PayPalCheckoutButtons;
```

## 🛒 Flujo Completo de Checkout

```jsx
// pages/Checkout.jsx
import { useState } from 'react';
import axios from 'axios';
import PayPalCheckout from '../components/PayPalCheckout';

const Checkout = () => {
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(false);

  const createOrder = async (orderData) => {
    try {
      setLoading(true);
      const token = localStorage.getItem('authToken');

      // Crear orden en tu backend
      const response = await axios.post(
        'http://localhost:8080/api/orders',
        orderData,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      setOrder(response.data);
      setLoading(false);
      
    } catch (error) {
      console.error('Error creando orden:', error);
      setLoading(false);
    }
  };

  return (
    <div className="checkout-page">
      <h1>Checkout</h1>
      
      {!order ? (
        <div>
          {/* Formulario de información del cliente */}
          <button onClick={() => createOrder(/* datos del formulario */)}>
            Continuar al Pago
          </button>
        </div>
      ) : (
        <div>
          <h2>Resumen de la Orden</h2>
          <p>Total: ${order.total}</p>
          
          {/* Botón de PayPal */}
          <PayPalCheckout 
            orderId={order.id}
            amount={order.total}
          />
        </div>
      )}
    </div>
  );
};

export default Checkout;
```

## 🎨 Estilos CSS

```css
/* styles/paypal.css */

.paypal-checkout {
  margin: 20px 0;
}

.paypal-button {
  background-color: #0070ba;
  color: white;
  border: none;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  width: 100%;
  max-width: 400px;
}

.paypal-button:hover {
  background-color: #005ea6;
}

.paypal-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #d32f2f;
  margin-top: 10px;
  padding: 10px;
  background-color: #ffebee;
  border-radius: 4px;
}

.payment-processing,
.payment-success,
.payment-error,
.payment-cancel {
  text-align: center;
  padding: 40px;
  max-width: 600px;
  margin: 0 auto;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #0070ba;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 20px auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.payment-details {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
  text-align: left;
}
```

## 🔗 Configuración de Rutas (React Router)

```jsx
// App.jsx con rutas
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Checkout from './pages/Checkout';
import PaymentSuccess from './pages/PaymentSuccess';
import PaymentCancel from './pages/PaymentCancel';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/payment/success" element={<PaymentSuccess />} />
        <Route path="/payment/cancel" element={<PaymentCancel />} />
        {/* Otras rutas */}
      </Routes>
    </BrowserRouter>
  );
}
```

## 🔧 Servicio de API (Opcional)

```javascript
// services/paypalService.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const getAuthHeaders = () => {
  const token = localStorage.getItem('authToken');
  return {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  };
};

export const paypalService = {
  createPayPalOrder: async (orderId, amount, currency = 'USD') => {
    const response = await axios.post(
      `${API_BASE_URL}/paypal/create-order`,
      { orderId, amount, currency },
      { headers: getAuthHeaders() }
    );
    return response.data;
  },

  capturePayPalOrder: async (paypalOrderId) => {
    const response = await axios.post(
      `${API_BASE_URL}/paypal/capture-order/${paypalOrderId}`,
      {},
      { headers: getAuthHeaders() }
    );
    return response.data;
  },

  getPayPalOrderDetails: async (paypalOrderId) => {
    const response = await axios.get(
      `${API_BASE_URL}/paypal/order/${paypalOrderId}`,
      { headers: getAuthHeaders() }
    );
    return response.data;
  }
};
```

## 📱 Ejemplo con Context API

```jsx
// context/PaymentContext.jsx
import { createContext, useContext, useState } from 'react';
import { paypalService } from '../services/paypalService';

const PaymentContext = createContext();

export const PaymentProvider = ({ children }) => {
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [paymentError, setPaymentError] = useState(null);

  const initiatePayPalPayment = async (orderId, amount) => {
    try {
      setPaymentLoading(true);
      setPaymentError(null);
      
      const response = await paypalService.createPayPalOrder(orderId, amount);
      
      // Redirigir a PayPal
      window.location.href = response.approvalUrl;
      
    } catch (error) {
      setPaymentError(error.message);
      setPaymentLoading(false);
    }
  };

  const capturePayment = async (paypalOrderId) => {
    try {
      setPaymentLoading(true);
      setPaymentError(null);
      
      const response = await paypalService.capturePayPalOrder(paypalOrderId);
      
      setPaymentLoading(false);
      return response;
      
    } catch (error) {
      setPaymentError(error.message);
      setPaymentLoading(false);
      throw error;
    }
  };

  return (
    <PaymentContext.Provider value={{
      paymentLoading,
      paymentError,
      initiatePayPalPayment,
      capturePayment
    }}>
      {children}
    </PaymentContext.Provider>
  );
};

export const usePayment = () => {
  const context = useContext(PaymentContext);
  if (!context) {
    throw new Error('usePayment debe usarse dentro de PaymentProvider');
  }
  return context;
};
```

## 🚀 Uso del Context

```jsx
// En tu componente
import { usePayment } from '../context/PaymentContext';

const CheckoutButton = ({ orderId, amount }) => {
  const { initiatePayPalPayment, paymentLoading, paymentError } = usePayment();

  return (
    <div>
      <button 
        onClick={() => initiatePayPalPayment(orderId, amount)}
        disabled={paymentLoading}
      >
        {paymentLoading ? 'Procesando...' : 'Pagar con PayPal'}
      </button>
      {paymentError && <p className="error">{paymentError}</p>}
    </div>
  );
};
```

## ✅ Checklist de Implementación

- [ ] Instalar dependencias necesarias
- [ ] Configurar rutas de éxito y cancelación
- [ ] Implementar componente de checkout
- [ ] Crear páginas de éxito y cancelación
- [ ] Manejar estados de carga y errores
- [ ] Probar flujo completo en sandbox
- [ ] Validar redirecciones
- [ ] Implementar manejo de errores
- [ ] Agregar feedback visual al usuario
- [ ] Probar en diferentes navegadores

## 🎯 Mejoras Opcionales

1. **Loading States**: Mostrar indicadores de carga
2. **Error Handling**: Mensajes de error amigables
3. **Retry Logic**: Reintentar pagos fallidos
4. **Analytics**: Trackear eventos de pago
5. **Notifications**: Notificar al usuario por email
6. **Mobile Responsive**: Optimizar para móviles

---

**Nota**: Este es un ejemplo básico. Adapta el código según las necesidades específicas de tu aplicación.

