import { createContext, useContext, useState } from 'react'

const CartContext = createContext()

export function CartProvider({ children }) {
  const [cart, setCart] = useState([])

  const addToCart = (product) => {
    setCart(prev => {
      const existing = prev.find(i => i.id === product.id)
      if (existing) {
        return prev.map(i =>
          i.id === product.id
            ? { ...i, quantity: i.quantity + 1 }
            : i
        )
      }
      return [...prev, { ...product, quantity: 1 }]
    })
  }

  const removeFromCart = (id) => {
    setCart(prev => prev.filter(i => i.id !== id))
  }

  const updateQuantity = (id, quantity) => {
    if (quantity <= 0) {
      removeFromCart(id)
      return
    }
    setCart(prev =>
      prev.map(i => i.id === id ? { ...i, quantity } : i)
    )
  }

  const clearCart = () => setCart([])

  const total = cart.reduce(
    (sum, item) => sum + item.price * item.quantity, 0
  )

  const itemCount = cart.reduce(
    (sum, item) => sum + item.quantity, 0
  )

  return (
    <CartContext.Provider value={{
      cart, addToCart, removeFromCart,
      updateQuantity, clearCart, total, itemCount
    }}>
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)