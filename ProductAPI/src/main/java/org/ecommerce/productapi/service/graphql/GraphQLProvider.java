package org.ecommerce.productapi.service.graphql;

import org.ecommerce.productapi.service.OrderService;
import org.ecommerce.productapi.service.PerfumeService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class GraphQLProvider {

    private final PerfumeService perfumeService;
    private final OrderService orderService;

    @Value("classpath:graphql/schemas.graphql")
    private Resource resource;

    @Getter
    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        try (InputStream is = resource.getInputStream()) {
            TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(new InputStreamReader(is, StandardCharsets.UTF_8));
            RuntimeWiring wiring = buildRuntimeWiring();
            GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
            graphQL = GraphQL.newGraphQL(schema).build();
        }
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("perfumes", perfumeService.getAllPerfumesByQuery())
                        .dataFetcher("perfumesIds", perfumeService.getAllPerfumesByIdsQuery())
                        .dataFetcher("perfume", perfumeService.getPerfumeByQuery())
                        .dataFetcher("orders", orderService.getAllOrdersByQuery())
                        .dataFetcher("ordersByEmail", orderService.getUserOrdersByEmailQuery()))
                .build();
    }
}