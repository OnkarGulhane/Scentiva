import logging
import json
import re
import sys
from datetime import datetime
from typing import Any, Dict

SENSITIVE_PATTERNS = [
    (re.compile(r'(?i)\b(bearer\s+)([A-Za-z0-9\-\._~\+\/=]+)'), r'\1[REDACTED]'),
    (re.compile(r'(?i)\b(password|secret|token|authorization|apikey|api_key|key|jwt)\s*[:=]\s*(["\']?)(?:bearer\s+)?([^"\'\s,]+)\2'), r'\1: [REDACTED]'),
    (re.compile(r'\beyJ[A-Za-z0-9\-_]{10,}\b'), r'[REDACTED]'),
]

def mask_sensitive_data(message: str) -> str:
    """Mask passwords, tokens, API keys, and secret values in log messages."""
    masked = message
    for pattern, repl in SENSITIVE_PATTERNS:
        masked = pattern.sub(repl, masked)
    return masked

class StructuredJsonFormatter(logging.Formatter):
    """Custom JSON formatter for Scentiva AI Service logs."""
    
    def format(self, record: logging.LogRecord) -> str:
        log_obj: Dict[str, Any] = {
            "timestamp": datetime.utcnow().isoformat() + "Z",
            "level": record.levelname,
            "logger": record.name,
            "message": mask_sensitive_data(record.getMessage()),
            "module": record.module,
            "line": record.lineno,
        }
        
        if hasattr(record, "request_id"):
            log_obj["request_id"] = getattr(record, "request_id")
        if hasattr(record, "feature"):
            log_obj["feature"] = getattr(record, "feature")
        if hasattr(record, "duration_ms"):
            log_obj["duration_ms"] = getattr(record, "duration_ms")
        if hasattr(record, "user_id"):
            log_obj["user_id"] = getattr(record, "user_id")
        if record.exc_info:
            log_obj["exception"] = self.formatException(record.exc_info)
            
        return json.dumps(log_obj)

def get_logger(name: str = "scentiva-ai") -> logging.Logger:
    """Factory function for retrieving configured structured logger."""
    logr = logging.getLogger(name)
    if not logr.handlers:
        handler = logging.StreamHandler(sys.stdout)
        handler.setFormatter(StructuredJsonFormatter())
        logr.addHandler(handler)
        logr.setLevel(logging.INFO)
        logr.propagate = False
    return logr

logger = get_logger("scentiva-ai")
