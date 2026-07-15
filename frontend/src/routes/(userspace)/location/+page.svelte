<script lang="ts">
	import { locations } from '$lib/stores/locations.svelte';
	import { onMount } from 'svelte';
	import { enhance } from '$app/forms';
	import Error from '$lib/Error.svelte';

	onMount(() => {
		locations.load(true);
	});

	let { data, form } = $props();
</script>


<div class="flex flex-col">
	{#if form?.error && form?.type == "location"}
		<Error code={form?.error}></Error>
	{/if}
	{#if data.user?.role === "ROLE_ADMIN"}
		<div class="flex flex-row flex-wrap justify-center gap-2 h-min">
			<form action="?/addLocation" method="POST" class="flex flex-col card gap-2 h-fit" use:enhance>
				<input class="input clickable" name="name" placeholder="Szkoła Podstawowa nr 720" required type="text">
				<input class="input clickable" name="shortname" placeholder="SP720" required type="text">
				<button class="clickable click cursor-pointer" type="submit">Dodaj</button>
			</form>
			<div class="card flex flex-row max-w-1/2 flex-wrap justify-center gap-2">
				{#each $locations.data as location (location.id)}
					<a class="text-sm rounded-xl clickable link"
					   href={`/location/${location.id}`}>{location.name}</a>
				{/each}
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