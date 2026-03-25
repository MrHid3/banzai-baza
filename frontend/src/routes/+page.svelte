<script lang="ts">
	import welcomeFallback from '$lib/images/svelte-welcome.png';
	import welcome from '$lib/images/svelte-welcome.webp';
	import Counter from './Counter.svelte';
	import {browser} from "$app/environment";
	import {goto} from "$app/navigation";
	import {onMount} from "svelte";

	onMount(() => {
		if(browser)
			goto("/login")
	})
</script>