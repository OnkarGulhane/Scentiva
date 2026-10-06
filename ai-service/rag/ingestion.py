import json
import os
from typing import List, Dict, Any, Optional
from models.domain import RagDocumentChunk
from app_logging.logger import logger

class DocumentIngestionPipeline:
    """
    RAG ingestion pipeline for Scentiva.
    Loads, cleans, chunks, and metadata-tags approved products, brands, FAQs, and policies.
    """
    
    def __init__(self, data_dir: Optional[str] = None):
        self.data_dir = data_dir or os.path.join(os.path.dirname(__file__), "..", "knowledge_data")

    def load_and_chunk_all(self) -> List[RagDocumentChunk]:
        """Load all approved knowledge sources and chunk into vector-ready documents."""
        chunks: List[RagDocumentChunk] = []
        chunks.extend(self._ingest_products())
        chunks.extend(self._ingest_brands())
        chunks.extend(self._ingest_policies())
        chunks.extend(self._ingest_faqs())
        logger.info(f"Ingested {len(chunks)} total RAG document chunks")
        return chunks

    def _ingest_products(self) -> List[RagDocumentChunk]:
        path = os.path.join(self.data_dir, "products.json")
        if not os.path.exists(path):
            return []
        with open(path, "r", encoding="utf-8") as f:
            products = json.load(f)
            
        chunks = []
        for p in products:
            content = (
                f"Perfume Flacon: {p['name']} by {p['brand_name']}.\n"
                f"Tagline: {p.get('tagline', '')}\n"
                f"Category: {p.get('category', 'Unisex')} | Gender: {p.get('gender', 'Unisex')}.\n"
                f"Concentration: {p.get('concentration', 'EDP')} | Fragrance Families: {', '.join(p.get('fragrance_families', []))}.\n"
                f"Olfactory Notes:\n"
                f"- Top Notes: {', '.join(p.get('top_notes', []))}\n"
                f"- Heart Notes: {', '.join(p.get('heart_notes', []))}\n"
                f"- Base Notes: {', '.join(p.get('base_notes', []))}\n"
                f"Sillage: {p.get('sillage', 'Moderate')} | Longevity: {p.get('longevity', '8 Hours')}.\n"
                f"Best Seasons: {', '.join(p.get('season', []))} | Occasions: {', '.join(p.get('occasion', []))}.\n"
                f"Starting Price: ₹{p.get('starting_price')} (MRP ₹{p.get('mrp')}).\n"
                f"Description: {p.get('description', '')}\n"
                f"Artisanal Story: {p.get('story', '')}"
            )
            chunks.append(RagDocumentChunk(
                doc_id=f"doc-prod-{p['id']}",
                source_type="product",
                title=f"{p['brand_name']} {p['name']}",
                content=content,
                metadata={
                    "product_id": p["id"],
                    "numeric_id": p.get("numeric_id"),
                    "slug": p.get("slug"),
                    "brand_id": p.get("brand_id"),
                    "brand_name": p.get("brand_name"),
                    "price": p.get("starting_price"),
                    "fragrance_families": p.get("fragrance_families", []),
                    "occasion": p.get("occasion", []),
                    "season": p.get("season", []),
                    "gender": p.get("gender", "Unisex"),
                    "longevity": p.get("longevity", "")
                }
            ))
        return chunks

    def _ingest_brands(self) -> List[RagDocumentChunk]:
        path = os.path.join(self.data_dir, "brands.json")
        if not os.path.exists(path):
            return []
        with open(path, "r", encoding="utf-8") as f:
            brands = json.load(f)
            
        chunks = []
        for b in brands:
            content = (
                f"Luxury Fragrance House: {b['name']} ({b['origin_country']}).\n"
                f"Heritage & Tier: {b.get('tier', 'Prestige')}.\n"
                f"Description: {b.get('description', '')}"
            )
            chunks.append(RagDocumentChunk(
                doc_id=f"doc-brand-{b['id']}",
                source_type="brand",
                title=b["name"],
                content=content,
                metadata={
                    "brand_id": b["id"],
                    "brand_name": b["name"],
                    "origin_country": b.get("origin_country"),
                    "tier": b.get("tier")
                }
            ))
        return chunks

    def _ingest_policies(self) -> List[RagDocumentChunk]:
        path = os.path.join(self.data_dir, "policies.json")
        if not os.path.exists(path):
            return []
        with open(path, "r", encoding="utf-8") as f:
            policies = json.load(f)
            
        chunks = []
        for idx, pol in enumerate(policies):
            content = (
                f"SCENTIVA Policy: {pol['topic']}\n"
                f"Title: {pol['title']}\n"
                f"Category: {pol['category']}\n"
                f"Official Protocol: {pol['content']}"
            )
            chunks.append(RagDocumentChunk(
                doc_id=f"doc-policy-{idx+1}",
                source_type="policy",
                title=pol["title"],
                content=content,
                metadata={
                    "category": pol["category"],
                    "topic": pol["topic"]
                }
            ))
        return chunks

    def _ingest_faqs(self) -> List[RagDocumentChunk]:
        path = os.path.join(self.data_dir, "faqs.json")
        if not os.path.exists(path):
            return []
        with open(path, "r", encoding="utf-8") as f:
            faqs = json.load(f)
            
        chunks = []
        for idx, f_item in enumerate(faqs):
            content = (
                f"Frequently Asked Question:\n"
                f"Question: {f_item['question']}\n"
                f"Category: {f_item.get('category', 'general')}\n"
                f"Verified Answer: {f_item['answer']}"
            )
            chunks.append(RagDocumentChunk(
                doc_id=f"doc-faq-{idx+1}",
                source_type="faq",
                title=f_item["question"],
                content=content,
                metadata={
                    "category": f_item.get("category", "general"),
                    "question": f_item["question"]
                }
            ))
        return chunks
