from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from sklearn.metrics.pairwise import cosine_similarity
from typing import List
import numpy as np

app = FastAPI(title="Sentinel Recommendation Service")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

PRODUCTS = [
    {"id": "1", "name": "iPhone 15", "category": "Electronics", "price": 79999, "tags": ["phone", "apple", "smartphone"]},
    {"id": "2", "name": "Samsung Galaxy S25", "category": "Electronics", "price": 69999, "tags": ["phone", "samsung", "smartphone"]},
    {"id": "3", "name": "OnePlus 12", "category": "Electronics", "price": 59999, "tags": ["phone", "oneplus", "smartphone"]},
    {"id": "4", "name": "MacBook Pro", "category": "Electronics", "price": 199999, "tags": ["laptop", "apple", "computer"]},
    {"id": "5", "name": "Dell XPS 15", "category": "Electronics", "price": 149999, "tags": ["laptop", "dell", "computer"]},
    {"id": "6", "name": "Sony WH-1000XM5", "category": "Electronics", "price": 29999, "tags": ["headphones", "sony", "audio"]},
    {"id": "7", "name": "JBL Flip 6", "category": "Electronics", "price": 9999, "tags": ["speaker", "jbl", "audio"]},
    {"id": "8", "name": "Nike Running Shoes", "category": "Clothing", "price": 7999, "tags": ["shoes", "nike", "sports"]},
    {"id": "9", "name": "Adidas T-Shirt", "category": "Clothing", "price": 1999, "tags": ["tshirt", "adidas", "casual"]},
    {"id": "10", "name": "Levi's Jeans", "category": "Clothing", "price": 3999, "tags": ["jeans", "levis", "casual"]},
    {"id": "11", "name": "Clean Code", "category": "Books", "price": 499, "tags": ["programming", "book", "software"]},
    {"id": "12", "name": "System Design Interview", "category": "Books", "price": 699, "tags": ["programming", "book", "system-design"]},
]

ALL_TAGS = sorted(set(tag for p in PRODUCTS for tag in p["tags"]))
ALL_CATEGORIES = sorted(set(p["category"] for p in PRODUCTS))

def product_to_vector(product):
    tag_vec = [1 if tag in product["tags"] else 0 for tag in ALL_TAGS]
    cat_vec = [1 if product["category"] == cat else 0 for cat in ALL_CATEGORIES]
    price_normalized = [product["price"] / 200000]
    return np.array(tag_vec + cat_vec + price_normalized)

PRODUCT_VECTORS = {p["id"]: product_to_vector(p) for p in PRODUCTS}

class RecommendRequest(BaseModel):
    product_id: str
    limit: int = 4

class ProductRec(BaseModel):
    id: str
    name: str
    category: str
    price: float
    similarity_score: float

class RecommendResponse(BaseModel):
    product_id: str
    recommendations: List[ProductRec]

@app.get("/health")
def health():
    return {"status": "UP", "products_loaded": len(PRODUCTS)}

@app.get("/recommend/{product_id}", response_model=RecommendResponse)
def recommend_get(product_id: str, limit: int = 4):
    if product_id not in PRODUCT_VECTORS:
        recs = PRODUCTS[:limit]
        return RecommendResponse(
            product_id=product_id,
            recommendations=[
                ProductRec(id=p["id"], name=p["name"],
                          category=p["category"], price=p["price"],
                          similarity_score=0.5)
                for p in recs
            ]
        )

    target_vec = PRODUCT_VECTORS[product_id]
    scores = []

    for pid, vec in PRODUCT_VECTORS.items():
        if pid == product_id:
            continue
        sim = cosine_similarity([target_vec], [vec])[0][0]
        scores.append((pid, sim))

    scores.sort(key=lambda x: x[1], reverse=True)
    top = scores[:limit]

    product_map = {p["id"]: p for p in PRODUCTS}
    recommendations = [
        ProductRec(
            id=pid,
            name=product_map[pid]["name"],
            category=product_map[pid]["category"],
            price=product_map[pid]["price"],
            similarity_score=round(float(sim), 4)
        )
        for pid, sim in top
    ]

    return RecommendResponse(
        product_id=product_id,
        recommendations=recommendations
    )