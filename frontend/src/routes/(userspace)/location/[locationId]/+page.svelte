<script lang="ts">
	import { locations } from '$lib/stores/locations.svelte';
	import { onMount } from 'svelte';
	import { enhance } from '$app/forms';
	import Error from '$lib/Error.svelte';

	const { data, form, params } = $props();

	onMount(() => {
		locations.load(true);
	});

	let location = $derived(data.location);
</script>

<div class="flex flex-col">
	{#if form?.error && form?.type == "location"}
		<Error code={form?.error}></Error>
	{/if}
	{#if data.user?.role === "ROLE_ADMIN"}
		<div class="flex flex-row flex-wrap justify-center gap-2 h-min">
			<form class="flex flex-col card justify-center h-fit gap-2" method="POST" action="?/update" use:enhance={() => {
        return async ({ update }) => {
            await update({ reset: false });
        };
    }}>
				<input type="hidden" name="id" value={location.id} />
				<div>
					<input type="text" class="clickable input" id="nameInput" name="name" minlength="1" value={location.name}
					       placeholder="Nazwa">
				</div>
				<div>
					<input type="text" class="clickable input" id="shortnameInput" name="shortname" minlength="1"
					       value={location.shortname} placeholder="Skrót">
				</div>
				<button type="submit" class="clickable click cursor-pointer">Zapisz</button>
			</form>
			<div class="card flex flex-row max-w-1/2 flex-wrap justify-center gap-2">
				{#each $locations.data as location (location.id)}
					{#if location.id == params.locationId}
						<a class="text-sm rounded-xl clickable link text-(--active-foreground)! bg-(--active)!"
						   href={`/location/${location.id}`}>{location.name}</a>
					{:else}
						<a class="text-sm rounded-xl clickable link"
						   href={`/location/${location.id}`}>{location.name}</a>
					{/if}
				{/each}
				<a href="/location" class="text-sm rounded-xl clickable link">Powrót</a>
			</div>
		</div>
	{/if}

</div>

<style>
    @import "tailwindcss";

    .card {
        @apply
        p-4
        rounded-lg shadow-lg shadow-slate-50/60
        h-min
        bg-(--background-secondary)
        ;
    }

    .clickable {
        @apply
        p-1
        rounded-lg shadow-xl hover:shadow-lg shadow-slate-800/20
        duration-500
        text-(--text-primary)
        hover:-translate-y-1/12
        ;
    }

    .input {
        @apply
        bg-(--input)
        ;
    }

    .click {
        @apply
        bg-(--click)
        cursor-pointer
        ;
    }

    .link {
        @apply
        bg-(--link)
        ;
    }
</style>