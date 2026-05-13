import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load : PageLoad = async (event) => {

    const appUsers = await event.fetch("/api/appUser");
    const locations = await event.fetch("/api/location/all");
    const categories = await event.fetch("/api/memberCategory");

    if (appUsers.ok && locations.ok && categories.ok) {

        return {
            locations: await locations.json(),
            users: await appUsers.json(),
            categories: await categories.json(),
        };
    }

}