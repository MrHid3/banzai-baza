<script lang="ts">
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import github from '$lib/images/github.svg';
	import logo from '$lib/images/svelte-logo.svg';
	import {logout} from "$lib/stores/auth.ts";
</script>

<header>
<!--	<div class="corner">-->
<!--		<a href="https://svelte.dev/docs/kit">-->
<!--			<img src={logo} alt="SvelteKit" />-->
<!--		</a>-->
<!--	</div>-->
<!---->
	<nav>
		<svg viewBox="0 0 2 3" aria-hidden="true">
			<path d="M0,0 L1,2 C1.5,3 1.5,3 2,3 L2,0 Z" />
		</svg>
		<ul>
			<li aria-current={page.url.pathname === '/baza' ? 'page' : undefined}>
				<a href={resolve('/baza')}>Baza</a>
			</li>
		</ul>
		<svg viewBox="0 0 2 3" aria-hidden="true">
			<path d="M0,0 L0,3 C0.5,3 0.5,3 1,2 L2,0 Z" />
		</svg>
	</nav>

	<button class="corner" onclick={() => logout()}>
		<svg viewBox="0 0 2 3" aria-hidden="true">
			<path d="M0,0 L1,2 C1.5,3 1.5,3 2,3 L2,0 Z" />
		</svg>
		<div id="logout">Wyloguj się</div>
<!--		<a href="https://github.com/sveltejs/kit">-->
<!--			<img src={github} alt="GitHub" />-->
<!--		</a>-->
	</button>
</header>

<style>
	header {
		display: flex;
		justify-content: center;
	}

	.corner {
		/*width: 5em;*/
		height: fit-content;
		display: flex;
		flex-direction: row;
		width: fit-content;
		background-color: transparent;
		border: none;
		cursor: pointer;
		position: absolute;
		top: 0;
		right: 0;
	}

	#logout{
		background-color: var(--background-special);
		color: white;
		border: none;
		padding: 0 10px;
		text-align: center;
		vertical-align: middle;
		align-items: center;
		line-height: 3em;
		font-weight: 800;
		height: 3em;
	}

	.corner svg{
		fill: var(--background-special);
		/*position: relative;*/
		/*top: 50%;*/
		/*left: 50%;*/
	}

	.corner a {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 100%;
	}

	.corner img {
		width: 2em;
		height: 2em;
		object-fit: contain;
	}

	nav {
		display: flex;
		justify-content: center;
		--background: #b3b3b3;
	}

	svg {
		width: 2em;
		height: 3em;
		display: block;
		z-index: 1000;
	}

	path {
		fill: var(--background);
	}

	ul {
		position: relative;
		padding: 0;
		margin: 0;
		height: 3em;
		display: flex;
		justify-content: center;
		align-items: center;
		list-style: none;
		background: var(--background);
		background-size: contain;
	}

	li {
		position: relative;
		height: 100%;
	}

	li[aria-current='page']::before {
		--size: 6px;
		content: '';
		width: 0;
		height: 0;
		position: absolute;
		top: 0;
		left: calc(50% - var(--size));
		border: var(--size) solid transparent;
		border-top: var(--size) solid var(--color-theme-1);
	}

	nav a {
		display: flex;
		height: 100%;
		align-items: center;
		padding: 0 0.5rem;
		color: var(--color-text);
		font-weight: 700;
		font-size: 0.8rem;
		text-transform: uppercase;
		letter-spacing: 0.1em;
		text-decoration: none;
		transition: color 0.2s linear;
	}


	a:hover {
		color: var(--color-theme-1);
	}
</style>
