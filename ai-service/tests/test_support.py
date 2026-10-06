import pytest
from agents.support_agent import support_agent
from models.schemas import SupportChatRequest

def test_support_policy_return_inquiry():
    """Test customer support return policy inquiry."""
    req = SupportChatRequest(message="What is your return policy for perfumes?")
    res = support_agent.process_support_query(req)
    
    assert res.queryType == "KNOWLEDGE"
    assert "7-Day" in res.reply or "return" in res.reply.lower()
    assert res.policyCitation is not None

def test_support_live_order_status():
    """Test customer support live order tracking lookup."""
    req = SupportChatRequest(
        message="Where is my order SCT-82454?",
        orderNumber="SCT-82454"
    )
    res = support_agent.process_support_query(req)
    
    assert res.queryType == "LIVE_ORDER"
    assert res.orderDetails is not None
    assert res.orderDetails.orderNumber == "SCT-82454"
    assert "SCT-82454" in res.reply
