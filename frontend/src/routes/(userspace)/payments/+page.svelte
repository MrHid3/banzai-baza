<script lang="ts">

    import LocationSelect from "$lib/LocationSelect.svelte";

    const {data, form} = $props();

    let members = $state(data.members ?? []);
    let filteredMembers = $state(members)

    $effect(() => {
        members = data.members ?? [];
    })

    $effect(() => {
        let result = members;
        if (selectedLocation != null) {
            result = result.filter((a) => a.location.id == selectedLocation?.id);
        }
        if (memberTextFilter.length >= 3) {
            result = result.filter((a) =>
                (a.name != null && a.name.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                (a.surname != null && a.surname.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                a.email.toLowerCase().includes(memberTextFilter.toLowerCase()) ||
                (a.phoneNumber != null && a.phoneNumber.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                (a.comment != null && a.comment.toLowerCase().includes(memberTextFilter.toLowerCase())));
        }

        filteredMembers = result;
    })

    let memberTextFilter = $state("");
    let selectedLocation = $state(null);

    const monthNames = ["Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"]

    const currentMonth = new Date().getMonth()
</script>

<div id="filterHolder">
    <!--    TODO: dodaj lupe-->
    <span>Znajdź:</span>
    <input bind:value={memberTextFilter} id="textFilterInput" type="text"/>
    <span>Filtruj po lokalizacji:</span>
    <LocationSelect all={true} bind:location={selectedLocation} short={false}></LocationSelect>
    {#if form?.error}
        <span class="error">{form.error}</span>
    {/if}
</div>
<table>
    <thead>
    <tr>
        <td>Imię</td>
        <td>Nazwisko</td>
        <td>Lokalizacja</td>
        <td>{monthNames[currentMonth - 2]}</td>
        <td>{monthNames[currentMonth - 1]}</td>
        <td>{monthNames[currentMonth]}</td>
    </tr>
    </thead>
    <tbody>
    {#each filteredMembers as member, index (index)}
        <tr>
            <td>{member.name}</td>
            <td>{member.surname}</td>
            <td>{member.location.shortname}</td>
<!--            <td>{}</td>-->
<!--            <td>{}</td>-->
<!--            <td>{}</td>-->
        </tr>
    {/each}
    </tbody>
</table>

<style>
    thead td {
        text-transform: math-auto;
    }
</style>