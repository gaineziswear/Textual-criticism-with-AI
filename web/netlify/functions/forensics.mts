import type { Config } from "@netlify/functions";

export default async (req: Request) => {
  const incoming = new URL(req.url);
  const suffix = incoming.pathname.replace(/^\/api\/forensics/, "") || "/manuscripts";
  const upstreamBase = Netlify.env.get("FORENSICS_API_BASE") || "https://hadith-linguistic-forensics-am2zb5.v2.appdeploy.ai";
  const upstream = new URL(suffix + incoming.search, upstreamBase.endsWith("/") ? upstreamBase : upstreamBase + "/");

  try {
    const response = await fetch(upstream, {
      method: req.method,
      headers: req.headers,
      body: req.method === "GET" || req.method === "HEAD" ? undefined : await req.arrayBuffer(),
      signal: AbortSignal.timeout(30000),
    });

    const contentType = response.headers.get("content-type") || "application/json";
    return new Response(response.body, {
      status: response.status,
      headers: { "content-type": contentType, "cache-control": "no-store" },
    });
  } catch (error) {
    const message = error instanceof Error ? error.message : "Upstream research service unavailable.";
    return Response.json(
      { error: "Research service unavailable.", detail: message },
      { status: 502, headers: { "cache-control": "no-store" } },
    );
  }
};

export const config: Config = {
  path: ["/api/forensics", "/api/forensics/*"],
};
