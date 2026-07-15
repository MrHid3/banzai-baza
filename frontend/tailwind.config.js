/** @type {import('tailwindcss').Config} */
module.exports = {
	content: ['./src/**/*.{ts,tsx,svelte}'],
	theme: {
		colors: {
			'bg-primary': '#ededed',
			'bg-secondary': '#dadada',
			'bg-special': '#141414',
			'text-primary': '#4f4f4f',
			'text-secondary': '#bbbbbb',
			'border': '#777777',
			'accent': '#4c4c4c',
		},
		// fontFamily: {
		// 	sans: ['Graphik', 'sans-serif'],
		// 	serif: ['Merriweather', 'serif'],
		// },
		// extend: {
		// 	spacing: {
		// 		'8xl': '96rem',
		// 		'9xl': '128rem',
		// 	},
		// 	borderRadius: {
		// 		'4xl': '2rem',
		// 	}
		}
	}