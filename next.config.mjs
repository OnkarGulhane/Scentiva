/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
      },
    ],
  },
  transpilePackages: ['three', '@react-three/fiber', '@react-three/drei', 'gsap', 'lenis', 'lucide-react'],
};

export default nextConfig;
