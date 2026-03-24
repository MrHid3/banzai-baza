<script lang="ts">
    import Member from "./Member.svelte";

    let { data } = $props();

    let members = $state(data.members);
    let filteredMembers = $state(members)

    $effect(() => {
        let result = members;
        if(selectedLocationId != -1){
            result = result.filter((a) => a.location.id == selectedLocationId);
        }
        if(memberTextFilter.length >= 3){
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
    let selectedLocationId = $state(-1);
</script>

<div class="container">
    <div class="filterHolder">
        <input type="text" bind:value={memberTextFilter}/>
        <select name="locations" id="locationSelect" bind:value={selectedLocationId}>
            <option value={-1} selected>Wszystkie</option>
            {#each data.locations as location, index(index)}
                <option value={location.id}>{location.name}</option>
            {/each}
        </select>
    </div>
    <div class="membersTable" style="--number-of-elements-minus-four: {members.length + 1 - 4}">
        <div class="header">
            <span class="data">Imię</span>
            <span class="data">Nazwisko</span>
            <span class="data">Email</span>
            <span class="data">Nr telefonu</span>
            <span class="data">Lokalizacja</span>
            <span class="data">Cena/mieś.</span>
            <span class="data">Komentarz</span>
            <div class="plus">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512" width="30" height="30" style="fill: white;"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path d="M256 64c0-17.7-14.3-32-32-32s-32 14.3-32 32l0 160-160 0c-17.7 0-32 14.3-32 32s14.3 32 32 32l160 0 0 160c0 17.7 14.3 32 32 32s32-14.3 32-32l0-160 160 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-160 0 0-160z"/></svg>
            </div>
        </div>
        {#each filteredMembers as member, index (index)}
            <Member componentClass="Member" member={member}></Member>
        {/each}
    </div>
</div>

<style>
    .membersTable > * {
        border-color: #444444;
        border-style: solid;
        border-width: 0 0 2px 0;
    }

    .membersTable{
        display: table;
        text-align: center;
        line-height: 2.4em;
        font-size: 1.2em;
        margin: 0 auto;
    }

    .data{
        padding: 5px;
        display: table-cell;
        vertical-align: middle;
        width: auto;
    }

    .header{
        display: table-row;
        position: relative;
    }

    .plus{
        position: absolute;
        right: 10px;
        top: 0;
        align-items: center;
        display: flex;
        height: 100%;
        padding: 20px;
        cursor: pointer;
    }

    .filterHolder{
        display: flex;
        flex-direction: row;
    }

    .addFilter{
        padding: 0 10px
    }

</style>
