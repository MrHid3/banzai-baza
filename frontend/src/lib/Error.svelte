<script lang="ts">

    let { code }  : {code: string} = $props();

    const map : Map<string, string> = new Map([
        ["MEMBER_HAS_LOCATION", "Do lokalizacji są przypisani członkowie"],
        ["USER_NOT_FOUND", "Nie ma takiego użytkownika"],
        ["USER_NOT_ACTIVE", "Użytkownik nie jest aktywny"],
        ["INVALID_PASSWORD", "Zły email lub hasło"]
    ])

</script>

<div class="error">
    {map.get(code) ?? code}
</div>

<style>
    .error{
        color: red;
        text-align: center;
    }
</style>