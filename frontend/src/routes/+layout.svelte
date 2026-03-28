<script lang="ts">
    import "./normalize.css";

    let { children } = $props();

</script>

<main>

    {@render children()}
</main>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap');

    :global(:root){
        --font-body:
                Arial, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell,
                'Open Sans', 'Helvetica Neue', sans-serif;
        --font-mono: 'Fira Mono', monospace;
        --color-background-primary: #121212;
        --color-background-secondary: #252525;
        --color-text-primary: #E0E0E0;
        --color-text-secondary: #B0B0B0;
        --color-border: #444444;
        --accent: #888888;
        --background-special: #b3b3b3;
        /*font-family: var(--font-body);*/
        color: var(--color-text-primary);
        background-color: var(--color-background-primary);
    }

    :global(*){
        box-sizing: border-box;
        margin: 0;
        padding: 0;
	    font-family: 'Ubuntu', sans-serif;
    }

    main{
        background-color: var(--background-color);
        color: var(--color-text-primary);
    }
</style>