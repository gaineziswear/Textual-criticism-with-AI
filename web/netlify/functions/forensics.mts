import type { Config } from "@netlify/functions";

export default async (req: Request) => {
  const incoming = new URL(req.url);
  const route = incoming.searchParams.get("route") || incoming.pathname;
  const upstreamBase =
    Netlify.env.get("FORENSICS_API_BASE") ||
    "https://hadith-linguistic-forensics-am2zb5.v2.appdeploy.ai";
  const upstream = new URL(
    route,
    upstreamBase.endsWith("/") ? upstreamBase : upstreamBase + "/",
  );

  try {
    const body =
      req.method === "GET" || req.method === "HEAD"
        ? undefined
        : await req.arrayBuffer();

    const headers = new Headers();
    const contentType = req.headers.get("content-type");
    const accept = req.headers.get("accept");
    if (contentType) headers.set("content-type", contentType);
    if (accept) headers.set("accept", accept);

    const response = await fetch(upstream, {
      method: req.method,
      headers,
      body,
      signal: AbortSignal.timeout(30000),
    });

    const responseHeaders = new Headers();
    const responseContentType = response.headers.get("content-type");
    if (responseContentType) {
      responseHeaders.set("content-type", responseContentType);
    } else {
      responseHeaders.set("content-type", "application/json");
    }
    responseHeaders.set("cache-control", "no-store");

    return new Response(response.body, {
      status: response.status,
      headers: responseHeaders,
    });
  } catch (error) {
    const message =
      error instanceof Error
        ? error.message
        : "Upstream research service unavailable.";

    return Response.json(
      {
        error: "Research service unavailable.",
        detail: message,
      },
      {
        status: 502,
        headers: { "cache-control": "no-store" },
      },
    );
  }
};

export const config: Config = {
  path: "/api/*",
};
