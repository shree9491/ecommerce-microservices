import { useState, useEffect } from 'react'
import { Package, Clock, CheckCircle, XCircle } from 'lucide-react'
import { orderAPI } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { Navigate } from 'react-router-dom'

const statusConfig = {
  PENDING:   { icon: Clock,        color: 'text-amber-500',  bg: 'bg-amber-50',  label: 'Pending' },
  COMPLETED: { icon: CheckCircle,  color: 'text-green-500',  bg: 'bg-green-50',  label: 'Completed' },
  FAILED:    { icon: XCircle,      color: 'text-red-500',    bg: 'bg-red-50',    label: 'Failed' },
  PAID:      { icon: CheckCircle,  color: 'text-blue-500',   bg: 'bg-blue-50',   label: 'Paid' },
}

export default function Orders() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const { user } = useAuth()

  if (!user) return <Navigate to="/login" />

  useEffect(() => {
    orderAPI.getByUser(user.userId)
      .then(res => setOrders(res.data))
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
    </div>
  )

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">My Orders</h1>

      {orders.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <Package size={48} className="mx-auto mb-3 opacity-30"/>
          <p>No orders yet</p>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map(order => {
            const status = statusConfig[order.status] || statusConfig.PENDING
            const StatusIcon = status.icon
            return (
              <div key={order.id} className="bg-white border border-gray-200 rounded-xl p-6">
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <p className="text-xs text-gray-400 mb-1">Order ID</p>
                    <p className="font-mono text-sm text-gray-700">{order.id}</p>
                  </div>
                  <span className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-medium ${status.bg} ${status.color}`}>
                    <StatusIcon size={14}/>
                    {status.label}
                  </span>
                </div>

                <div className="space-y-2 mb-4">
                  {order.items?.map((item, i) => (
                    <div key={i} className="flex justify-between text-sm">
                      <span className="text-gray-600">{item.productName} × {item.quantity}</span>
                      <span className="text-gray-900 font-medium">₹{Number(item.totalPrice).toLocaleString()}</span>
                    </div>
                  ))}
                </div>

                <div className="flex items-center justify-between pt-4 border-t border-gray-100">
                  <span className="text-sm text-gray-400">
                    {new Date(order.createdAt).toLocaleDateString('en-IN', {
                      day: 'numeric', month: 'short', year: 'numeric'
                    })}
                  </span>
                  <span className="font-bold text-gray-900">
                    Total: ₹{Number(order.totalAmount).toLocaleString()}
                  </span>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}