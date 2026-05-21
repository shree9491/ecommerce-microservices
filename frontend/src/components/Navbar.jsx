import { Link, useNavigate } from 'react-router-dom'
import { ShoppingCart, Package, LogOut, User, Store, LayoutDashboard } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const { itemCount } = useCart()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2 font-bold text-xl text-indigo-600">
          <Store size={24} />
          ShopHub
        </Link>

        <div className="flex items-center gap-6">
          <Link to="/products" className="text-gray-600 hover:text-indigo-600 transition-colors">
            Products
          </Link>

          {user && (
            <Link to="/orders" className="text-gray-600 hover:text-indigo-600 transition-colors flex items-center gap-1">
              <Package size={18} />
              Orders
            </Link>
          )}

          {user && (
            <Link to="/admin" className="text-gray-600 hover:text-indigo-600 transition-colors flex items-center gap-1">
              <LayoutDashboard size={18} />
              Admin
            </Link>
          )}

          <Link to="/cart" className="relative text-gray-600 hover:text-indigo-600 transition-colors">
            <ShoppingCart size={22} />
            {itemCount > 0 && (
              <span className="absolute -top-2 -right-2 bg-indigo-600 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {itemCount}
              </span>
            )}
          </Link>

          {user ? (
            <div className="flex items-center gap-3">
              <span className="text-sm text-gray-600 flex items-center gap-1">
                <User size={16} />
                {user.username}
              </span>
              <button
                onClick={handleLogout}
                className="flex items-center gap-1 text-sm text-red-500 hover:text-red-700 transition-colors"
              >
                <LogOut size={16} />
                Logout
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-indigo-700 transition-colors"
            >
              Login
            </Link>
          )}
        </div>
      </div>
    </nav>
  )
}