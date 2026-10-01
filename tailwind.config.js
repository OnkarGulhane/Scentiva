/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          plum: {
            950: '#321027',
            900: '#451333',
            800: '#5B1B43',
            700: '#742653',
          },
          blush: {
            300: '#E9B7D8',
            200: '#F2D2E7',
            100: '#FAEAF4',
          },
          rose: {
            500: '#B85B88',
          },
          gold: {
            500: '#C7A66A',
            100: '#F5EBD7',
          }
        },
        neutral: {
          0: '#FFFFFF',
          50: '#FAF8F7',
          100: '#F2EEEC',
          200: '#E4DCDA',
          400: '#A69A98',
          600: '#6D6262',
          800: '#342C30',
          950: '#1D171B',
        },
        semantic: {
          success: '#26734D',
          warning: '#94630B',
          error: '#B4233A',
          info: '#315F9B',
        }
      },
      fontFamily: {
        serif: ['"Cormorant Garamond"', 'Georgia', 'serif'],
        sans: ['Inter', '-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'Roboto', 'sans-serif'],
      },
      boxShadow: {
        'card': '0 2px 12px rgba(44, 20, 35, 0.06)',
        'card-hover': '0 10px 28px rgba(44, 20, 35, 0.12)',
        'popover': '0 16px 48px rgba(29, 23, 27, 0.16)',
        'modal': '0 24px 80px rgba(29, 23, 27, 0.24)',
        'glow-gold': '0 0 25px rgba(199, 166, 106, 0.25)',
        'glow-blush': '0 0 30px rgba(233, 183, 216, 0.25)',
      },
      borderRadius: {
        'xs': '4px',
        'sm': '8px',
        'md': '12px',
        'lg': '18px',
        'xl': '24px',
      },
      animation: {
        'float': 'float 6s ease-in-out infinite',
        'pulse-subtle': 'pulseSubtle 3s ease-in-out infinite',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px) rotate(0deg)' },
          '50%': { transform: 'translateY(-10px) rotate(1deg)' },
        },
        pulseSubtle: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.85' },
        }
      }
    },
  },
  plugins: [],
}
