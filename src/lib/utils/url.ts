/**
 * URL and Redirect Safety Utilities
 * Protects against Open Redirect vulnerabilities by strictly allowing internal relative paths.
 */

export function getSafeRedirectUrl(url: string | null | undefined, fallback: string = '/checkout'): string {
  if (!url || typeof url !== 'string') {
    return fallback;
  }

  const trimmed = url.trim();

  // Disallow empty or javascript/data URIs
  if (!trimmed || trimmed.startsWith('javascript:') || trimmed.startsWith('data:')) {
    return fallback;
  }

  // Must start with single slash and NOT double slash // (protocol-relative URL)
  if (trimmed.startsWith('/') && !trimmed.startsWith('//') && !trimmed.startsWith('/\\')) {
    return trimmed;
  }

  return fallback;
}
