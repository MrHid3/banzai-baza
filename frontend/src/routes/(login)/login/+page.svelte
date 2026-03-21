<script lang="ts">
    import {enhance} from "$app/forms"
    import {goto} from "$app/navigation";
    import {isAuthenticated, token} from '../../../stores/auth.ts'
    import {get} from "svelte/store";

    let { form } = $props()

    const passwordPattern = ".{8,}";

    let showPassword = $state(false);

    $effect(() => { if( form?.token ) {
        token.set(form.token);
        goto('/db');
    }})

    if(get(isAuthenticated)){
        goto('/db');
    }

</script>

<form method="POST" action="?/login" use:enhance>
    <input type="email" name="email">
    <input type={showPassword? "text" : "password"} name="password" pattern={passwordPattern}>
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
    }

    input:invalid:not(:focus){
        border: 1px solid red;
    }

    form:has(input:invalid:not(:focus)) #send{
        opacity: 0.7;
    }
</style>