<script lang="ts">
    import {enhance} from "$app/forms"
    import {goto} from "$app/navigation";

    let { form } = $props()

    const passwordPattern = ".{8,}";

    let showPassword = $state(false);

    $effect(() => { if( form?.token ) {
        goto('/db');
    }})

    //TODO: Fancy loading type shit
</script>
<svelte:head>
    <title>Login</title>
</svelte:head>

    <form method="POST" action="?/login" use:enhance>
    <input type="email" name="email" placeholder="Email">
    <input type={showPassword? "text" : "password"} name="password" pattern={passwordPattern} placeholder="Hasło">
    <label for="show">
        <input type="checkbox" name="show" bind:checked={showPassword}>
        Pokaż hasło
    </label>
    <button id="send" type="submit">Zaloguj się</button>

    {#if form?.error}
        {form.error}
    {/if}
</form>

<style>
    *{
        background-color: var(--color-background-primary);
        color: var(--color-text-primary);
    }

    form{
        position: absolute;
        top: 30%;
        left: 50%;
        transform: translate(-50%, 50%);
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    input:not([type=checkbox]), #send{
        width: 15vw;
        min-width: 200px;
        padding: 10px;
        border: 1px solid var(--color-border)
    }

    input:invalid:not(:focus){
        border: 1px solid red;
    }

    form:has(input:invalid:not(:focus)) #send{
        opacity: 0.7;
    }
</style>