/** @type {import('tailwindcss').Config} */
export default {
  content: ["./src/main/resources/**/*.{html,js}"],
  theme: {
    extend: {
      colors: {
        /* Remap the neutral scale to the requested premium slate palette.
           Because every template already uses gray-* utilities, this shifts
           the entire app (light + dark) to the design-spec colors in one place:
             light  -> bg #F8FAFC, border #E2E8F0, secondary #64748B, text #0F172A
             dark   -> bg #0B1220, surface #111827, cards/borders #1E293B/#334155,
                       secondary #94A3B8, text #F8FAFC (never pure black) */
        gray: {
          /* Softer light-mode surfaces so the app isn't a harsh white sheet:
             page backgrounds (50/100) are a gentle cool gray, while cards stay
             white (bg-white) and pop with clear separation. */
          50: "#EEF2F6",
          100: "#E6EBF1",
          200: "#E2E8F0",
          300: "#CBD5E1",
          400: "#94A3B8",
          500: "#64748B",
          600: "#475569",
          700: "#334155",
          800: "#1E293B",
          900: "#111827",
          950: "#0B1220",
        },
      },
      boxShadow: {
        card: "0 1px 2px 0 rgb(15 23 42 / 0.04), 0 1px 3px 0 rgb(15 23 42 / 0.06)",
        "card-hover":
          "0 8px 24px -6px rgb(15 23 42 / 0.12), 0 2px 6px -2px rgb(15 23 42 / 0.08)",
      },
    },
  },
  plugins: [],
  darkMode: "selector",
};
