<script lang="ts">
    import {resolve} from '$app/paths';
    import {page} from '$app/state';
    import {logout} from "$lib/stores/auth";

    let {user} = $props();
    let menuOpen = $state(false);

    function toggleMenu() {
        menuOpen = !menuOpen;
    }

    function closeMenu() {
        menuOpen = false;
    }
</script>

<header>
    <!-- Mobile bar: only visible on small screens -->
    <div class="mobile-bar">
        <button
                class="hamburger-btn"
                onclick={toggleMenu}
                aria-label="Toggle menu"
                aria-expanded={menuOpen}
        >
            <span class:open={menuOpen}></span>
            <span class:open={menuOpen}></span>
            <span class:open={menuOpen}></span>
        </button>
    </div>

    <!-- Desktop nav (unchanged) -->
    <nav>
        <svg aria-hidden="true" viewBox="0 0 2 3">
            <path d="M0,0 L1,2 C1.5,3 1.5,3 2,3 L2,0 Z"/>
        </svg>
        <ul>
            <li aria-current={page.url.pathname === '/payments' ? 'page' : undefined}>
                <a href={resolve('/payments')}>Płatności</a>
            </li>
            <li aria-current={page.url.pathname === '/db' ? 'page' : undefined}>
                <a href={resolve('/db')}>Baza</a>
            </li>
            <li aria-current={page.url.pathname === '/multipliers' ? 'page' : undefined}>
                <a href={resolve('/multipliers')}>Mnożniki</a>
            </li>
            <li aria-current={page.url.pathname === '/breakdown' ? 'page' : undefined}>
                <a href={resolve('/breakdown')}>Podsumowanie</a>
            </li>
            <li aria-current={page.url.pathname === '/sms' ? 'page' : undefined}>
                <a href={resolve('/sms')}>SMS</a>
            </li>
            {#if user?.role === "ROLE_ADMIN"}
                <li aria-current={page.url.pathname === '/users' ? 'page' : undefined}>
                    <a href={resolve('/users')}>Zarządzanie</a>
                </li>
            {/if}
            <li aria-current={page.url.pathname.startsWith('/location') ? 'page' : undefined}>
                <a href={resolve('/location')}>Lokalizacje</a>
            </li>
        </ul>
        <svg aria-hidden="true" viewBox="0 0 2 3">
            <path d="M0,0 L0,3 C0.5,3 0.5,3 1,2 L2,0 Z"/>
        </svg>
    </nav>

    <button class="corner" onclick={() => logout()} aria-label="Logout">
        <svg aria-hidden="true" viewBox="0 0 2 3">
            <path d="M0,0 L1,2 C1.5,3 1.5,3 2,3 L2,0 Z"/>
        </svg>
        <span id="logout">Wyloguj się</span>
    </button>

    <!-- Mobile dropdown menu -->
    {#if menuOpen}
        <nav class="mobile-menu" role="navigation">
            <a href={resolve('/payments')} onclick={closeMenu}>Płatności</a>
            <a href={resolve('/db')} onclick={closeMenu}>Baza</a>
            <a href={resolve('/multipliers')} onclick={closeMenu}>Mnożniki</a>
            <a href={resolve('/breakdown')} onclick={closeMenu}>Podsumowanie</a>
            <a href={resolve('/sms')} onclick={closeMenu}>SMS</a>
            {#if user?.role === "ROLE_ADMIN"}
                <a href={resolve('/users')} onclick={closeMenu}>Zarządzanie</a>
            {/if}
            <a href={resolve('/location')} onclick={closeMenu}>Lokalizacje</a>
            <button class="mobile-logout" onclick={() => { logout(); closeMenu(); }}>
                Wyloguj się
            </button>
        </nav>
    {/if}
</header>

<style>
    :root{
        --background: var(--background-secondary);
    }

    nav {
        display: flex;
        justify-content: center;
    }
    header {
        z-index: 76897;
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

    #logout {
        background-color: var(--background);
        color: var(--text-primary-dark);
        border: none;
        padding: 0 10px;
        text-align: center;
        vertical-align: middle;
        align-items: center;
        line-height: 3em;
        font-weight: 800;
        height: 3em;
        transition-duration: 0.4s;
    }

    #logout:hover{
        color: var(--text-primary-dark);
    }

    .corner svg {
        fill: var(--background-secondary);
        /*position: relative;*/
        /*top: 50%;*/
        /*left: 50%;*/
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

    a{
        /*color: var(--background-primary);*/
        color: var(--text-primary-dark)
    }

    a:hover{
        /*color: var(--background-secondary)*/
        color: var(--text-primary-dark)
    }

    li[aria-current='page'] a {
        color: var(--active) !important;
    }

    nav a {
        display: flex;
        height: 100%;
        align-items: center;
        padding: 0 0.5rem;
        /*color: var(--color-text);*/
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

    .mobile-bar{
        display: none;
    }

    @media screen and (width <= 1000px){
        /* --- hamburger button --- */
        .mobile-bar {
            display: none;
        }

        .hamburger-btn {
            background: transparent;
            border: none;
            cursor: pointer;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 0 1rem;
            height: 3em;
            gap: 5px;
        }

        .hamburger-btn span {
            display: block;
            width: 22px;
            height: 2px;
            background: var(--background-primary);
            border-radius: 1px;
            transition: transform 0.25s, opacity 0.25s;
        }

        .hamburger-btn span:nth-child(1).open {
            transform: translateY(7px) rotate(45deg);
        }

        .hamburger-btn span:nth-child(2).open {
            opacity: 0;
        }

        .hamburger-btn span:nth-child(3).open {
            transform: translateY(-7px) rotate(-45deg);
        }

        /* --- mobile dropdown --- */
        .mobile-menu {
            display: flex;
            flex-direction: column;
            width: 100%;
            background: var(--background);
            position: absolute;
            top: 3em;
            left: 0;
            z-index: 999;
        }

        .mobile-menu a,
        .mobile-logout {
            color: var(--background-primary);
            font-weight: 700;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 0.1em;
            padding: 0.9rem 1.2rem;
            text-decoration: none;
            border-top: 1px solid rgba(255, 255, 255, 0.15);
            display: block;
            text-align: left;
            width: 100%;
            background: transparent;
            border-left: none;
            border-right: none;
            border-bottom: none;
            cursor: pointer;
        }

        .mobile-logout {
            border-top: 2px solid rgba(255, 255, 255, 0.3);
        }

        .mobile-title {
            color: var(--background-primary);
            font-weight: 800;
            font-size: 0.85rem;
            letter-spacing: 0.05em;
            padding-left: 0.5rem;
        }

        @media screen and (width <= 1000px) {
            header {
                display: flex;
                flex-direction: column;
                position: relative;
            }

            nav:not(.mobile-menu),
            .corner {
                display: none;
            }

            .mobile-bar {
                display: flex;
                align-items: center;
                justify-content: space-between;
                width: 100%;
                background: var(--background);
                height: 3em;
                box-sizing: border-box;
            }
        }
    }
</style>
