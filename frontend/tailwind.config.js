/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      fontFamily: {
        mono: ['"JetBrains Mono"', '"Share Tech Mono"', 'ui-monospace', 'monospace']
      },
      colors: {
        hud: {
          bg: '#000b18',
          line: '#4df',
          text: '#c8e6ff'
        }
      }
    }
  },
  plugins: []
}
