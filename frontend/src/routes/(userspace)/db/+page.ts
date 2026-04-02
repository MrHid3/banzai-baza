import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load: PageLoad = async (event) => {

    const res1 = await event.fetch(`/api/member`, {method: "GET"})
    const res2 = await event.fetch("/api/location/all", {method: "GET"})

    if(res1.ok && res2.ok){
        const res1json = await res1.json();
        const res2json = await res2.json();
        return { members: res1json, locations: res2json };
    }
};