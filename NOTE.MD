# Weather Service Implementation Notes
## Components
### WeatherClient
The WeatherClient serves as an interface within our domain layer, acting as a boundary between our application and external weather data providers. A concrete adapter for the OpenMeteo API in the infrastructure layer will handle all aspects of external communication. This client construct API requests, processing responses, and transform the raw data into our domain objects.
Rather than throwing exceptions for failed API calls, the client returns an Optional that represents either successful data retrieval or its absence.  
### WeatherCache
WeatherCache interface in the domain layer provide an abstraction that supports multiple storage implementations. An implementation for database persistence is provided and possible extension with Redis or others in-memory storage are possible with the possibility to switch between them through configuration properties.
The cache stores successful API responses, allowing the service to continue functioning during API outages or performance degradation. The caching logic is decoupled from both the weather service and client components.
### WeatherService
WeatherService orchestrates the interaction between the WeatherClient and WeatherCache. The service implements a fallback approach that prioritizes new data.
When processing a request, the service first attempts to fetch fresh data from the API. If successful, this data is both returned to the user and stored in the cache. If API call fail, the service falls back to valid cached data less than 10 minutes old. If no valid cache exists, the service will use stale data to prevent service failure.
The service also manages temperature unit conversions when needed, ensuring users receive data in their format regardless of how it was originally stored or retrieved.
### Cache Eviction
To maintain database performance, a scheduled cache eviction strategy is implemented. A background task runs daily to remove entries older than 30 days, preventing growth of the cache table while retaining historical data for analysis.
The eviction process is designed to minimize database load and operates independently of the main request processing path.
## Implementation Details
### Caching Strategy
The caching strategy centers around 10-minute validity period that aligns with frontend refresh cycle. This synchronization ensures that users receive new data under normal circumstances while providing resilience during API disruptions.
When the external API is functioning properly, users see current weather conditions refreshed every 10 minutes. During outages, the service degrades by serving  older data rather than failing outright.
### Database Considerations
Compound indices on latitude and longitude coordinates is implemented to enable efficient location-based lookups.
Timestamp columns are also indexed to support identification of stale cache entries during both normal operation and eviction processes.
### Why Database Caching?
We chose database caching as our primary strategy since the application already used PostgreSQL, this approach leveraged existing infrastructure without requiring DevOps involvement or new dependencies.
The database provides a natural shared state between multiple application instances, ensuring consistent cache behavior in a distributed environment.
### Future Improvements
Implementing PostGIS for geospatial indexing would optimize location-based queries, particularly for proximity searches beyond exact coordinate matches. A circuit breaker pattern using Resilience4j would provide more sophisticated handling of API failures with automatic recovery.
Adding a secondary weather provider would eliminate single-vendor dependency, background caching for popular locations could further reduce API calls and improve response times. 
