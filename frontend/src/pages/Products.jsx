import { useState, useEffect } from 'react'
import { ShoppingCart, Search, Package } from 'lucide-react'
import { productAPI } from '../services/api'
import api from '../services/api'
import axios from 'axios'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function Products() {
  const [products, setProducts] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [recommendations, setRecommendations] = useState([])
  const [selectedProduct, setSelectedProduct] = useState(null)
  const { addToCart } = useCart()
  const { user } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    productAPI.getAll()
      .then(res => setProducts(res.data))
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  const filtered = products.filter(p =>
    p.name.toLowerCase().includes(search.toLowerCase())
  )

  const fetchRecommendations = async (productId) => {
    try {
        const productIndex = products.findIndex(p => p.id === productId) + 1
        const res = await fetch(`http://localhost:8086/recommend/${productIndex}`)
        const data = await res.json()
        setRecommendations(data.recommendations || [])
        setSelectedProduct(productId)
    } catch (err) {
        console.error('Failed to fetch recommendations', err)
    }
}

  const handleAddToCart = (product) => {
    if (!user) { navigate('/login'); return }
    addToCart(product)
  }

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
    </div>
  )

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Products</h1>
        <div className="relative">
          <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
          <input
            type="text"
            placeholder="Search products..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="pl-10 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 w-64"
          />
        </div>
      </div>

      {filtered.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <Package size={48} className="mx-auto mb-3 opacity-30"/>
          <p>No products found</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {filtered.map(product => (
            <div key={product.id} className="bg-white border border-gray-200 rounded-xl overflow-hidden hover:shadow-md transition-shadow">
              <div className="bg-gradient-to-br from-indigo-50 to-purple-50 h-40 flex items-center justify-center">
                <Package size={48} className="text-indigo-300"/>
              </div>
              <div className="p-4">
                <span className="text-xs text-indigo-600 font-medium bg-indigo-50 px-2 py-1 rounded-full">
                  {product.categoryName}
                </span>
                <h3 className="font-semibold text-gray-900 mt-2 mb-1 line-clamp-1">
                  {product.name}
                </h3>
                <p className="text-xs text-gray-400 mb-3 line-clamp-2">
                  {product.description}
                </p>
                <div className="flex items-center justify-between">
                  <span className="text-lg font-bold text-gray-900">
                    ₹{Number(product.price).toLocaleString()}
                  </span>
                  <span className="text-xs text-gray-400">
                    Stock: {product.stockQuantity}
                  </span>
                </div>
                <button
                  onClick={() => fetchRecommendations(product.id)}
                  className="mt-3 w-full border border-indigo-200 text-indigo-600 py-2 rounded-lg text-sm font-medium hover:bg-indigo-50 transition-colors"
                >
                  🤖 Get Recommendations
                </button>
                <button
                  onClick={() => handleAddToCart(product)}
                  disabled={product.stockQuantity === 0}
                  className="mt-2 w-full bg-indigo-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors flex items-center justify-center gap-2 disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  <ShoppingCart size={16}/>
                  {product.stockQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {recommendations.length > 0 && (
        <div className="mt-12">
          <h2 className="text-xl font-bold text-gray-900 mb-2">
            🤖 AI Recommendations
          </h2>
          <p className="text-sm text-gray-400 mb-6">
            Based on your selected product
          </p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {recommendations.map(rec => (
              <div key={rec.id} className="bg-white border border-indigo-100 rounded-xl p-4 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs text-indigo-600 font-medium bg-indigo-50 px-2 py-1 rounded-full">
                    {rec.category}
                  </span>
                  <span className="text-xs text-green-600 bg-green-50 px-2 py-1 rounded-full">
                    {Math.round(rec.similarity_score * 100)}% match
                  </span>
                </div>
                <h3 className="font-semibold text-gray-900 mt-2">{rec.name}</h3>
                <p className="text-lg font-bold text-indigo-600 mt-1">
                  ₹{Number(rec.price).toLocaleString()}
                </p>
                <button
                  onClick={() => handleAddToCart({ ...rec, stockQuantity: 1 })}
                  className="mt-3 w-full bg-indigo-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors flex items-center justify-center gap-2"
                >
                  <ShoppingCart size={16}/>
                  Add to Cart
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}