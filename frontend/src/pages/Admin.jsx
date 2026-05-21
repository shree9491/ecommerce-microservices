import { useState, useEffect } from 'react'
import { adminAPI } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { Navigate } from 'react-router-dom'
import {
  ShoppingBag, Package, TrendingUp,
  Clock, CheckCircle, XCircle, AlertTriangle
} from 'lucide-react'

export default function Admin() {
  const { user } = useAuth()
  const [orderStats, setOrderStats] = useState(null)
  const [productStats, setProductStats] = useState(null)
  const [loading, setLoading] = useState(true)

  if (!user) return <Navigate to="/login" />

  useEffect(() => {
    Promise.all([
      adminAPI.getOrderStats(),
      adminAPI.getProductStats()
    ]).then(([orderRes, productRes]) => {
      setOrderStats(orderRes.data)
      setProductStats(productRes.data)
    }).catch(console.error)
    .finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
    </div>
  )

  const statCards = [
    {
      label: 'Total Orders',
      value: orderStats?.totalOrders ?? 0,
      icon: ShoppingBag,
      color: 'text-indigo-600',
      bg: 'bg-indigo-50'
    },
    {
      label: 'Total Revenue',
      value: `₹${Number(orderStats?.totalRevenue ?? 0).toLocaleString()}`,
      icon: TrendingUp,
      color: 'text-green-600',
      bg: 'bg-green-50'
    },
    {
      label: 'Pending Orders',
      value: orderStats?.pendingOrders ?? 0,
      icon: Clock,
      color: 'text-amber-600',
      bg: 'bg-amber-50'
    },
    {
      label: 'Completed',
      value: orderStats?.completedOrders ?? 0,
      icon: CheckCircle,
      color: 'text-green-600',
      bg: 'bg-green-50'
    },
    {
      label: 'Failed Orders',
      value: orderStats?.failedOrders ?? 0,
      icon: XCircle,
      color: 'text-red-600',
      bg: 'bg-red-50'
    },
    {
      label: 'Total Products',
      value: productStats?.totalProducts ?? 0,
      icon: Package,
      color: 'text-purple-600',
      bg: 'bg-purple-50'
    },
    {
      label: 'Out of Stock',
      value: productStats?.outOfStock ?? 0,
      icon: AlertTriangle,
      color: 'text-red-600',
      bg: 'bg-red-50'
    },
    {
      label: 'Low Stock',
      value: productStats?.lowStock ?? 0,
      icon: AlertTriangle,
      color: 'text-amber-600',
      bg: 'bg-amber-50'
    },
  ]

  const statusColors = {
    PENDING:   'bg-amber-50 text-amber-700',
    COMPLETED: 'bg-green-50 text-green-700',
    FAILED:    'bg-red-50 text-red-700',
    PAID:      'bg-blue-50 text-blue-700',
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">
        Admin Dashboard
      </h1>

      {/* Stat Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-10">
        {statCards.map(({ label, value, icon: Icon, color, bg }) => (
          <div key={label}
            className="bg-white border border-gray-200 rounded-xl p-4">
            <div className={`w-10 h-10 ${bg} rounded-lg flex items-center justify-center mb-3`}>
              <Icon size={20} className={color} />
            </div>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            <p className="text-sm text-gray-500 mt-1">{label}</p>
          </div>
        ))}
      </div>

      {/* Recent Orders */}
      <div className="bg-white border border-gray-200 rounded-xl overflow-hidden mb-8">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="font-semibold text-gray-900">Recent Orders</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Order ID</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Amount</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Status</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {orderStats?.recentOrders?.map(order => (
                <tr key={order.id} className="hover:bg-gray-50">
                  <td className="px-6 py-3 font-mono text-xs text-gray-600">
                    {order.id.slice(0, 8)}...
                  </td>
                  <td className="px-6 py-3 font-medium text-gray-900">
                    ₹{Number(order.totalAmount).toLocaleString()}
                  </td>
                  <td className="px-6 py-3">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColors[order.status] || 'bg-gray-100 text-gray-600'}`}>
                      {order.status}
                    </span>
                  </td>
                  <td className="px-6 py-3 text-gray-500">
                    {new Date(order.createdAt).toLocaleDateString('en-IN')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Product Inventory */}
      <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="font-semibold text-gray-900">Product Inventory</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Product</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Price</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Stock</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {productStats?.products?.map(product => (
                <tr key={product.id} className="hover:bg-gray-50">
                  <td className="px-6 py-3 font-medium text-gray-900">
                    {product.name}
                  </td>
                  <td className="px-6 py-3 text-gray-600">
                    ₹{Number(product.price).toLocaleString()}
                  </td>
                  <td className="px-6 py-3 text-gray-600">
                    {product.stockQuantity}
                  </td>
                  <td className="px-6 py-3">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      product.stockQuantity === 0
                        ? 'bg-red-50 text-red-700'
                        : product.stockQuantity <= 10
                        ? 'bg-amber-50 text-amber-700'
                        : 'bg-green-50 text-green-700'
                    }`}>
                      {product.stockQuantity === 0
                        ? 'Out of Stock'
                        : product.stockQuantity <= 10
                        ? 'Low Stock'
                        : 'In Stock'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}