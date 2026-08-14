<script lang="ts">
    let {children, user} = $props();

</script>

<main>

    {@render children(user = user)}
</main>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap');
    @import "tailwindcss";

    :global(:root) {
        --font-body: Arial, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell,
        'Open Sans', 'Helvetica Neue', sans-serif;
        --font-mono: 'Fira Mono', monospace;
        font-weight: bold;
        /*--color-background-primary: #121212;*/
        /*--color-background-secondary: #252525;*/
        /*--color-text-primary: #E0E0E0;*/
        /*--color-text-secondary: #B0B0B0;*/
        /*--color-border: #444444;*/
        /*--accent: #888888;*/
        /*--background-special: #b3b3b3;*/
        /*color: var(--color-text-primary);*/
        /*background-color: var(--color-background-primary);*/
        /*--background-primary: var(--color-slate-300);*/
        /*--background-secondary: var(--color-slate-400);*/
        /*--text-primary: var(--color-zinc-900);*/
        /*--text-secondary: var(--color-gray-700);*/
        /*--border: var(--color-slate-700);*/
        /*--accent: var(--color-gray-600);*/
        /*--background-special: var(--color-zinc-800);*/
        --background-primary: oklch(98.4% 0.003 247.858);
        --background-secondary: oklch(92.9% 0.013 255.508);
        --text-primary: oklch(96.8% 0.007 247.896);
        --text-primary-dark: oklch(27.9% 0.041 260.031);
        --text-secondary: oklch(37.2% 0.044 257.287);
        --border: #bbbbbb;
        --accent: #777777;
        --background-special: #4c4c4c;
        --click-dark: oklch(20.8% 0.042 265.755);
        --input-dark: oklch(27.9% 0.041 260.031);
        --link-dark: oklch(37.2% 0.044 257.287);
        --click: oklch(44.6% 0.043 257.281);
        --input: oklch(70.4% 0.04 256.788);
        --link: oklch(55.4% 0.046 257.417);
        --active-foreground: oklch(96.7% 0.003 264.542);
        --active: oklch(55.1% 0.027 264.364);
        --hover: oklch(86.9% 0.022 252.894);
        --hover-foreground: oklch(44.6% 0.043 257.281);
    }

    @theme{
        --background-primary: oklch(98.4% 0.003 247.858);
        --background-secondary: oklch(92.9% 0.013 255.508);
        --text-primary: oklch(96.8% 0.007 247.896);
        --text-primary-dark: oklch(27.9% 0.041 260.031);
        --text-secondary: oklch(37.2% 0.044 257.287);
        --border: #bbbbbb;
        --accent: #777777;
        --background-special: #4c4c4c;
        --click-dark: oklch(20.8% 0.042 265.755);
        --input-dark: oklch(27.9% 0.041 260.031);
        --link-dark: oklch(37.2% 0.044 257.287);
        --click: oklch(44.6% 0.043 257.281);
        --input: oklch(70.4% 0.04 256.788);
        --link: oklch(55.4% 0.046 257.417);
        --active-foreground: oklch(96.7% 0.003 264.542);
        --active: oklch(55.1% 0.027 264.364);
        --hover: oklch(86.9% 0.022 252.894);
        --hover-foreground: oklch(44.6% 0.043 257.281);
        --shd: var(--color-slate-50/60);
        --shd-dark: var(--color-slate-950/20);
    }

    :global(*) {
        box-sizing: border-box;
        font-family: 'Ubuntu', sans-serif;
    }

    main {
        color: var(--text-primary);
        @apply
        bg-radial from-slate-900 to-neutral-950
        ;
    }

    :global(input::-webkit-outer-spin-button),
    :global(input::-webkit-inner-spin-button) {
        -webkit-appearance: none;
        margin: 0;
    }

    /* Firefox */
    :global(input[type=number]) {
        -moz-appearance: textfield;
    }
</style>