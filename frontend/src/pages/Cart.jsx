import { Trash2, Plus, Minus, ShoppingBag } from 'lucide-react'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import { orderAPI } from '../services/api'
import { useNavigate, Link } from 'react-router-dom'
import { useState } from 'react'

export default function Cart() {
  const { cart, removeFromCart, updateQuantity, clearCart, total } = useCart()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)

  const handleCheckout = async () => {
    if (!user) { navigate('/login'); return }
    setLoading(true)
    try {
      const orderData = {
        userId: user.userId,
        items: cart.map(item => ({
          productId: item.id,
          productName: item.name,
          quantity: item.quantity,
          unitPrice: item.price
        }))
      }
      const res = await orderAPI.create(orderData)
      clearCart()
      navigate(`/orders`)
      alert(`Order placed! ID: ${res.data.id}`)
    } catch (err) {
      alert('Failed to place order: ' + (err.response?.data?.error || err.message))
    } finally {
      setLoading(false)
    }
  }

  if (cart.length === 0) return (
    <div className="max-w-2xl mx-auto px-4 py-16 text-center">
      <ShoppingBag size={64} className="mx-auto mb-4 text-gray-200"/>
      <h2 className="text-xl font-semibold text-gray-700 mb-2">Your cart is empty</h2>
      <p className="text-gray-400 mb-6">Add some products to get started</p>
      <Link to="/products" className="bg-indigo-600 text-white px-6 py-3 rounded-lg hover:bg-indigo-700 transition-colors">
        Browse Products
      </Link>
    </div>
  )

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Shopping Cart</h1>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-3">
          {cart.map(item => (
            <div key={item.id} className="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-4">
              <div className="bg-indigo-50 rounded-lg w-16 h-16 flex items-center justify-center flex-shrink-0">
                <ShoppingBag size={24} className="text-indigo-300"/>
              </div>
              <div className="flex-1">
                <h3 className="font-medium text-gray-900">{item.name}</h3>
                <p className="text-indigo-600 font-semibold">₹{Number(item.price).toLocaleString()}</p>
              </div>
              <div className="flex items-center gap-2">
                <button onClick={() => updateQuantity(item.id, item.quantity - 1)}
                  className="w-8 h-8 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50">
                  <Minus size={14}/>
                </button>
                <span className="w-8 text-center font-medium">{item.quantity}</span>
                <button onClick={() => updateQuantity(item.id, item.quantity + 1)}
                  className="w-8 h-8 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50">
                  <Plus size={14}/>
                </button>
              </div>
              <button onClick={() => removeFromCart(item.id)}
                className="text-red-400 hover:text-red-600 ml-2">
                <Trash2 size={18}/>
              </button>
            </div>
          ))}
        </div>

        <div className="bg-white border border-gray-200 rounded-xl p-6 h-fit">
          <h2 className="font-bold text-gray-900 mb-4">Order Summary</h2>
          <div className="space-y-2 mb-4">
            {cart.map(item => (
              <div key={item.id} className="flex justify-between text-sm text-gray-600">
                <span>{item.name} × {item.quantity}</span>
                <span>₹{(item.price * item.quantity).toLocaleString()}</span>
              </div>
            ))}
          </div>
          <div className="border-t border-gray-200 pt-4 mb-6">
            <div className="flex justify-between font-bold text-gray-900">
              <span>Total</span>
              <span>₹{total.toLocaleString()}</span>
            </div>
          </div>
          <button
            onClick={handleCheckout}
            disabled={loading}
            className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50"
          >
            {loading ? 'Placing order...' : 'Place Order'}
          </button>
        </div>
      </div>
    </div>
  )
}