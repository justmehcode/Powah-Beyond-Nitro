# License review — 2026-10-07

## Conclusion

The identified Powah-derived materials can be reused and modified under LGPLv3 when its conditions are met. The project owner has now supplied an original-source link for the crystal reference: a post by @yptsh (Yapetto). This improves attribution but does not establish a reuse license. Recolouring, resampling and outlining do not establish permission to redistribute the underlying artwork. This review is informational, not a legal opinion or unconditional non-infringement clearance.

## Crystal source update — 2026-10-07

- User-provided origin: https://x.com/yptsh/status/1613550925783920641
- Artist/account credit: Yapetto (@yptsh). The artist's own Bluesky profile identifies Yapetto and links the Twitter handle: https://bsky.app/profile/yptsh.bsky.social
- Direct X/Twitter retrieval returned no readable post text. The image match, post caption, replies and any post-specific reuse terms could not be independently verified through the available retrieval tools.
- No applicable permission grant was verified. An origin link and credit alone do not establish permission for recolouring, mod distribution or inclusion of editable PNGs in a public source repository. Permission covering those uses, or a suitable published license, would resolve the remaining question.
- This update changes only documentation and the source archive. The 1.0.0 JAR and in-game text are unchanged. NOTICE.md inside that JAR reflects the earlier Pinterest provenance; this document records the subsequently supplied source.

## Evidence checked

- The local exact Powah v6.2.10 checkout contains an LGPLv3 LICENSE. No separate asset-license file was found there. README credits owmii, Technici4n, Cyn and SammySemicolon. Applying the repository-wide license to these assets is the basis of this review; seek upstream clarification if contrary asset-specific terms appear.
- Powah's official CurseForge listing identifies LGPLv3: https://www.curseforge.com/minecraft/mc-mods/powah-rearchitected
- Exact source and notices: https://github.com/Technici4n/Powah/tree/v6.2.10
- GNU LGPLv3 terms: https://www.gnu.org/licenses/lgpl-3.0.html
- Incorporated GPLv3 terms: https://www.gnu.org/licenses/gpl-3.0.html
- GNU explanation for Java applications using LGPL libraries: https://www.gnu.org/licenses/lgpl-java.en.html

## Compliance work included

- Preserved LGPLv3 and added the full GPLv3 companion text required by LGPLv3 section 4(b).
- Credited upstream maintainers and named texture contributors.
- Documented resource modifications, dates and upstream revision.
- Included editable sources, modified resource files, build files and the asset generator.
- Kept Powah and Allthemodium binaries outside the mod JAR; users install them separately.
- Included Gradle's Apache license.
- Excluded externally supplied artwork from any assertion that this project grants its copyright permissions.

## Before distributing

1. Resolve the external artwork rights with the original creator and retain evidence, or use replacement artwork with verified licensing. Attribution alone is not permission.
2. Publish the complete corresponding source for the exact released JAR, including modified assets and necessary build scripts, with the license and notices. Provide source access alongside the binary release; a public repository/tag and matching source archive are a practical approach. A private local folder alone does not satisfy a public source-access offer.
3. Preserve applicable copyright/license notices and mark subsequent changes. Do not add restrictions that prevent recipients exercising LGPL/GPL rights, including modifying the covered portions and debugging those changes.
4. Keep external dependencies replaceable and provide the source/build route for rebuilding with modified LGPL components. The current Powah version pin is an implementation compatibility constraint, not a prohibition on modifying and rebuilding it.
5. Recheck terms of any future bundled third-party files. This package does not bundle dependency mod JARs.

No public upload was performed. This review cannot guarantee that all copyright, trademark or platform issues have been cleared.
