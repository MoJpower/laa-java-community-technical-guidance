# LAA Java Community Technical Guidance

[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/laa-java-community-technical-guidance/badge)](https://github-community.service.justice.gov.uk/repository-standards/laa-java-community-technical-guidance)

Guidance for Legal Aid Agency teams building and running Java services. The site is built with the GOV.UK tech docs stack so it looks and behaves like other Ministry of Justice guidance.

## What you'll find

- Principles for designing, building, testing, releasing, and operating Java services in LAA
- Recommendations on frameworks, tooling, observability, and security controls
- How to propose changes to the guidance and contribute examples from your own services

The published site will be available at <https://ministryofjustice.github.io/laa-java-community-technical-guidance/>.

## Getting started

You will need Docker to build or preview the site.

- Preview locally: `make preview` then visit <http://127.0.0.1:4567>
- Build the static site artifact: `make package`
- Check links before opening a pull request: `make link-check`

## Publishing

The site is published to GitHub Pages from the `main` branch via `.github/workflows/publish.yml`. Merging to `main` triggers a build and deploy using the pinned `tech-docs-github-pages-publisher` image.

## Contributing

- Open an issue or start a discussion if you want to change or add guidance.
- Keep examples concise and focus on reusable patterns rather than service-specific detail.
- Raise a pull request with your changes. PRs should include context on why the guidance is changing and any migration steps for existing services.
- Reviews are handled via CODEOWNERS (see `.github/CODEOWNERS`). Please prefer GitHub teams over individual users for ownership.

## Maintenance

- Dependabot is configured to update GitHub Actions weekly; adjust `.github/dependabot.yml` if we add new ecosystems.
- Keep this repo aligned with the [MoJ GitHub Repository Standards](https://github.com/ministryofjustice/github-repository-standards); the compliance badge above reflects the current status.
- The `config/tech-docs.yml` file controls the site title, navigation, and repository links.
- Keep the compliance badge above in sync with the repository name and remove it if the repository is private.

## Licence

Released under the [MIT License](LICENSE).
