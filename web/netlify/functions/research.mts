import type { Config } from "@netlify/functions";

const LANGUAGES = [
  "Persian","Classical Syriac","Nabataean Aramaic","Imperial Aramaic",
  "Biblical Aramaic","Jewish Babylonian Aramaic","Assyrian Neo-Aramaic",
  "Hebrew","Akkadian","Safaitic","Hismaic","Dadanitic",
  "Ancient North Arabian","Geʿez","Greek","Latin"
];

const SYSTEM = `You are the live AI philological engine for Hadith Truthfully.
Analyze HADITH only using neutral textual, linguistic, phonological and textual-critical methods.
Preserve the supplied witness exactly and never silently correct it.
Separate witness, normalization, transliteration, interpretation and inference.
A cognate is not automatically a borrowing. Similarity is not proof of contact or transmission.
A forced-language reading is an experiment, not proof of original composition.
Do not invent manuscripts, citations, scholars, dictionaries, external providers or evidence.
If evidence is insufficient, explicitly say so.
Return valid JSON only.`;

function parseOutput(data: any): any {
  const text = typeof data?.output_text === "string"
    ? data.output_text
    : (data?.output ?? []).flatMap((x: any) => x?.content ?? [])
        .map((x: any) => x?.text).filter(Boolean).join("\n");
  const cleaned = String(text).trim().replace(/^\`\`\`(?:json)?/i, "").replace(/\`\`\`$/, "").trim();
  try { return JSON.parse(cleaned); } catch {}
  const a = cleaned.indexOf("{"), b = cleaned.lastIndexOf("}");
  if (a >= 0 && b > a) return JSON.parse(cleaned.slice(a, b + 1));
  throw new Error("AI returned invalid JSON.");
}

async function sha256(text: string) {
  const b = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(text));
  return [...new Uint8Array(b)].map(x => x.toString(16).padStart(2,"0")).join("");
}

async function runAI(instructions: string, input: unknown) {
  const key = Netlify.env.get("openaiapikey");
  if (!key) throw new Error("OPENAI_API_KEY is not configured.");
  const model = Netlify.env.get("RESEARCH_MODEL") || "gpt-5.6-luna";
  const response = await fetch("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: { Authorization: `Bearer ${key}`, "Content-Type": "application/json" },
    body: JSON.stringify({
      model,
      store: false,
      reasoning: { effort: "medium" },
      instructions,
      input: JSON.stringify(input),
    }),
  });
  if (!response.ok) {
    const detail = await response.text();
    throw new Error(`AI provider returned HTTP ${response.status}: ${detail.slice(0, 500)}`);
  }
  return { model, result: parseOutput(await response.json()) };
}

export default async (req: Request) => {
  if (req.method === "OPTIONS") return new Response("ok");
  if (req.method !== "POST") return Response.json({ error: "POST required." }, { status: 405 });

  try {
    const body = await req.json();
    const text = typeof body.text === "string" ? body.text.trim() : "";
    const languages = Array.isArray(body.languages)
      ? body.languages.filter((x: unknown) => typeof x === "string")
      : [];
    const action = body.action || "dossier";

    if (!text) return Response.json({ error: "Witness text is required." }, { status: 400 });
    if (text.length > 18000) return Response.json({ error: "Witness text exceeds the 18,000 character research limit." }, { status: 400 });

    const witness_sha256 = await sha256(text);
    const common = { witness_exact: text, comparison_languages: languages };

    if (action === "forced-language") {
      const language = typeof body.language === "string" ? body.language : "";
      if (!LANGUAGES.includes(language)) return Response.json({ error: "Unsupported target language." }, { status: 400 });
      const ai = await runAI(
        SYSTEM + `
Perform a CONTROLLED FORCED-LANGUAGE EXPERIMENT under the selected target language.
Do not force matches where phonology, morphology or semantics fail.
Return keys: result, target_language, forced_text, transliteration, english_translation, arabic_literal_translation, back_translation, plausibility, aligned_spans, lexical_notes, warnings.
Plausibility is descriptive evidence strength only: Undetermined, Weak, Moderate or Strong.
`,
        { ...common, target_language: language, mode: body.mode === "evaluate" ? "evaluate" : "translate", supplied_translation: body.translation || "" }
      );
      return Response.json({ result: { ...ai.result, mode: body.mode === "evaluate" ? "evaluate" : "translate", language, witness_sha256, engine_version: "live-ai-2", ai_model: ai.model } });
    }

    if (action === "analyze") {
      const ai = await runAI(
        SYSTEM + `
Produce a structured linguistic analysis.
Return keys: summary, detected_language, detection_confidence, english_translation, arabic_literal_translation, transliteration, word_analysis, findings, methodological_notes.
word_analysis items must contain witness, normalized, transliteration, root_or_lemma, gloss, note.
findings items must contain text, language, confidence, classification, evidence, caveat.
Do not assert a root merely from superficial cross-language resemblance.
`,
        common
      );
      return Response.json({ analysis: { ...ai.result, witness_sha256, engine_version: "live-ai-2", ai_model: ai.model } });
    }

    const ai = await runAI(
      SYSTEM + `
Produce a complete forensic dossier.
Return keys: normalized, transliteration, detected_script, detected_language, detection_confidence, english_translation, arabic_literal_translation, word_analysis, root_fallbacks, textual_criticism, comparative_cognates, provider_comparison, synthesis, limitations.
comparative_cognates items: span, language, candidate, classification, evidence, caveat.
provider_comparison must identify only "Live AI philological perspective"; never fabricate multiple providers.
Only mention textual-critical hazards when applicable.
The synthesis must clearly separate observation from inference and state what additional evidence would be required.
`,
      common
    );
    return Response.json({ dossier: { witness: text, ...ai.result, witness_sha256, engine_version: "live-ai-2", ai_model: ai.model } });
  } catch (error) {
    return Response.json({
      error: error instanceof Error ? error.message : "Research engine failure."
    }, { status: 500 });
  }
};

export const config: Config = {
  path: "/api/research",
};
