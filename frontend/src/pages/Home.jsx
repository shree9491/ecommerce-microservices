import { Link } from 'react-router-dom'
import { ShoppingBag, Zap, Shield, Truck } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export default function Home() {
  const { user } = useAuth()

  return (
    <div>
      <div className="bg-gradient-to-br from-indigo-600 to-purple-700 text-white">
        <div className="max-w-6xl mx-auto px-4 py-20 text-center">
          <h1 className="text-5xl font-bold mb-4">Welcome to ShopHub</h1>
          <p className="text-xl text-indigo-200 mb-8 max-w-2xl mx-auto">
            Your one-stop destination for electronics, clothing, books and more.
            Fast delivery, secure payments.
          </p>
          <div className="flex gap-4 justify-center">
            <Link to="/products"
              className="bg-white text-indigo-600 px-8 py-3 rounded-xl font-semibold hover:bg-indigo-50 transition-colors flex items-center gap-2">
              <ShoppingBag size={20}/>
              Shop Now
            </Link>
            {!user && (
              <Link to="/login"
                className="border-2 border-white text-white px-8 py-3 rounded-xl font-semibold hover:bg-white/10 transition-colors">
                Get Started
              </Link>
            )}
          </div>
        </div>
      </div>

      <div className="max-w-6xl mx-auto px-4 py-16">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            { icon: Zap, title: 'Fast Delivery', desc: 'Get your orders delivered within 2-3 business days' },
            { icon: Shield, title: 'Secure Payments', desc: 'Your payment information is always safe with us' },
            { icon: Truck, title: 'Easy Returns', desc: '30-day hassle-free return policy on all products' },
          ].map(({ icon: Icon, title, desc }) => (
            <div key={title} className="text-center p-6 bg-white border border-gray-100 rounded-2xl hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-indigo-50 rounded-xl flex items-center justify-center mx-auto mb-4">
                <Icon size={24} className="text-indigo-600"/>
              </div>
              <h3 className="font-semibold text-gray-900 mb-2">{title}</h3>
              <p className="text-gray-500 text-sm">{desc}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}