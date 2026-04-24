<script lang="ts">
	import Header from './Header.svelte';

	let { data, children } = $props();
</script>
<div class="app">
	<Header user={data.user}/>

	<main>
		{@render children()}
	</main>

	<footer>
		<p>
<!--			visit <a href="https://svelte.dev/docs/kit">svelte.dev/docs/kit</a> to learn about SvelteKit-->
		</p>
	</footer>
</div>

<style>
	.app {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}

	main {
		flex: 1;
		display: flex;
		flex-direction: column;
		padding: 1rem;
		width: 80%;
		max-width: 80%;
		margin: 0 auto;
		box-sizing: border-box;
	}

	footer {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 12px;
	}

	/*footer a {*/
	/*	font-weight: bold;*/
	/*}*/

	@media (min-width: 480px) {
		footer {
			padding: 12px 0;
		}
	}

	@media screen and (width <= 1000px){
		main{
			max-width: 100%;
			width: 100%;
		}
	}

</style>
