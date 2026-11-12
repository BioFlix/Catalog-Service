# catalog-service

Film, sale og forestillinger (showtimes). Port **8082** (REST) og **9090** (gRPC). MongoDB `catalog_db`.
Demo-data indsættes automatisk ved første opstart (`app.seed-data=true`).

**Kør `mvn compile` første gang** – gRPC-klasserne genereres fra `src/main/proto/catalog.proto`.

## REST
| Metode | Sti |
|---|---|
| GET | `/api/movies?title=&genre=` · `/api/movies/{id}` · `/api/movies/{id}/showtimes` |
| GET | `/api/showtimes?date=2026-11-20` · `/api/showtimes/{id}` |
| GET | `/api/halls` · `/api/halls/{id}` |
| POST / PUT / DELETE | samme stier – TODO: kun admin |

## gRPC
`CatalogService.GetShowtime` – kaldes af booking-service. Kontrakten (`catalog.proto`) skal være identisk i booking-repoet.

## Events
Ingen (Catalog er kun synkron i diagrammet).
