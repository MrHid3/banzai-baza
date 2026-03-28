<script>
    let { showModal = $bindable(), header, children} = $props();

    let dialog = $state(); // HTMLDialogElement

    $effect(() => {
        if (showModal) dialog.showModal();
    });
</script>

<!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_noninteractive_element_interactions -->
<dialog
        bind:this={dialog}
        onclose={() => (showModal = false)}
        onclick={(e) => { if (e.target === dialog) dialog.close(); }}
>
    <div style="position: relative;">
        <div class="header">
            {@render header?.()}
        </div>
        <hr />
        {@render children?.()}
        <hr />
        <!-- svelte-ignore a11y_autofocus -->
    </div>
    <div class="buttons">
        <button autofocus onclick={() => dialog.close()}>Anuluj</button>
        <button>OK</button>
    </div>
</dialog>

<style>
    dialog {
        max-width: 32em;
        border-radius: 0.2em;
        border: none;
        padding: 0 2em 2em 2em;
        position: absolute;
        top: 40%;
        left: 50%;
        transform: translate(-50%, -50%);
        background-color: var(--color-background-primary);
        outline: 2px solid var(--color-border);
        width: fit-content;
        height: fit-content;
        color: var(--color-text);
    }
    div.buttons{
        display: flex;
        flex-direction: row;
    }
    div.header{
        /*border-color: var(--color-border);*/
        /*border-width: 0 0 2px 0;*/
        /*border-style: solid;*/
        margin: 1em 0;
        width: 100%;
    }
    .buttons button{
        padding: 0.8em;
        margin: 0.2em;
        background-color: var(--background-special);
        border: none;
        border-radius: 0.2em;
    }
    dialog::backdrop {
        background: rgba(0, 0, 0, 0.3);
    }
    dialog > div {
        padding: 1em;
    }
    dialog[open] {
        animation: zoom 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    }
    @keyframes zoom {
        from {
            transform: scale(0.95) translate(-50%, -50%);
        }
        to {
            transform: scale(1) translate(-50%, -50%);
        }
    }
    dialog[open]::backdrop {
        animation: fade 0.2s ease-out;
    }
    @keyframes fade {
        from {
            opacity: 0;
        }
        to {
            opacity: 1;
        }
    }
    button {
        display: block;
    }
</style>
