import { fetchWithAuth } from '$lib/fetchWithAuth.js';
import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load: PageLoad = async (event) => {

    const res1 = await fetchWithAuth(`/api/member/all`, {method: "GET"}, event.fetch)
    const res2 = await fetchWithAuth("/api/location/all", {method: "GET"}, event.fetch)
    if(res1.ok && res2.ok){
        const res1json = await res1.json();
        const res2json = await res2.json();
        return { members: res1json, locations: res2json };
    }
};