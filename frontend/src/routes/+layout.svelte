<script lang="ts">
    let {children} = $props();

</script>

<main>

    {@render children()}
</main>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap');

    :global(:root) {
        --font-body: Arial, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell,
        'Open Sans', 'Helvetica Neue', sans-serif;
        --font-mono: 'Fira Mono', monospace;
        /*--color-background-primary: #121212;*/
        /*--color-background-secondary: #252525;*/
        /*--color-text-primary: #E0E0E0;*/
        /*--color-text-secondary: #B0B0B0;*/
        /*--color-border: #444444;*/
        /*--accent: #888888;*/
        /*--background-special: #b3b3b3;*/
        /*color: var(--color-text-primary);*/
        /*background-color: var(--color-background-primary);*/
        --color-background-primary: #ededed;
        --color-background-secondary: #dadada;
        --color-text-primary: #141414;
        --color-text-secondary: #4f4f4f;
        --color-border: #bbbbbb;
        --accent: #777777;
        --background-special: #4c4c4c;
        color: var(--color-text-primary);
        background-color: var(--color-background-primary);
    }

    :global(*) {
        box-sizing: border-box;
        font-family: 'Ubuntu', sans-serif;
    }

    main {
        background-color: var(--background-color);
        color: var(--color-text-primary);
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