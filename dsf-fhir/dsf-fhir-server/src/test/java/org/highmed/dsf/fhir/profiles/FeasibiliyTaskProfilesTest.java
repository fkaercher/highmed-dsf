package org.highmed.dsf.fhir.profiles;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Collections;

import org.highmed.dsf.fhir.service.DefaultProfileValidationSupportWithCustomResources;
import org.highmed.dsf.fhir.service.SnapshotGenerator;
import org.highmed.dsf.fhir.service.SnapshotGenerator.SnapshotWithValidationMessages;
import org.highmed.dsf.fhir.service.SnapshotGeneratorImpl;
import org.highmed.dsf.fhir.service.StructureDefinitionReader;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;

public class FeasibiliyTaskProfilesTest
{
	private static final Logger logger = LoggerFactory.getLogger(FeasibiliyTaskProfilesTest.class);

	@Test(expected = RuntimeException.class)
	public void testGeneratSnapshotForExtendedTaskWithoutBaseTaskSnapshot() throws Exception
	{
		FhirContext context = FhirContext.forR4();
		StructureDefinitionReader reader = new StructureDefinitionReader(context);

		StructureDefinition baseTask = reader.readXml(Paths.get("src/test/resources/profiles/BaseTask.xml"));
		StructureDefinition extendedTask = reader.readXml(Paths.get("src/test/resources/profiles/ExtendedTask.xml"));

		SnapshotGenerator generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(Collections.emptyList(), Collections.emptyList(),
						Collections.emptyList()));

		SnapshotWithValidationMessages highmedTaskBaseSnapshot = generator.generateSnapshot(baseTask);

		assertNotNull(highmedTaskBaseSnapshot);
		assertNotNull(highmedTaskBaseSnapshot.getSnapshot());
		assertNotNull(highmedTaskBaseSnapshot.getMessages());
		assertTrue(highmedTaskBaseSnapshot.getMessages().isEmpty());

		generator.generateSnapshot(extendedTask);
	}

	@Test
	public void testGeneratSnapshotForExtendedTaskWithBaseTaskSnapshot() throws Exception
	{
		FhirContext context = FhirContext.forR4();
		StructureDefinitionReader reader = new StructureDefinitionReader(context);

		StructureDefinition baseTask = reader.readXml(Paths.get("src/test/resources/profiles/BaseTask.xml"));
		StructureDefinition extendedTask = reader.readXml(Paths.get("src/test/resources/profiles/ExtendedTask.xml"));

		SnapshotGenerator generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(Collections.emptyList(), Collections.emptyList(),
						Collections.emptyList()));

		SnapshotWithValidationMessages baseTaskSnapshotResult = generator.generateSnapshot(baseTask);

		assertNotNull(baseTaskSnapshotResult);
		assertNotNull(baseTaskSnapshotResult.getSnapshot());
		assertNotNull(baseTaskSnapshotResult.getMessages());
		assertTrue(baseTaskSnapshotResult.getMessages().isEmpty());

		StructureDefinition baseTaskSnapshot = baseTaskSnapshotResult.getSnapshot();
		baseTaskSnapshot.setSnapshot(
				new StructureDefinitionSnapshotComponent().setElement(baseTaskSnapshot.getSnapshot().getElement()));

		logger.info("Snapshot generated for StructureDefinition with url {}\n{}",
				baseTaskSnapshotResult.getSnapshot().getUrl(), context.newXmlParser().setPrettyPrint(true)
						.encodeResourceToString(baseTaskSnapshotResult.getSnapshot()));

		generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(
						Collections.singleton(baseTaskSnapshotResult.getSnapshot()), Collections.emptyList(),
						Collections.emptyList()));

		SnapshotWithValidationMessages extendedTaskSnaphotResult = generator
				.generateSnapshot(extendedTask);

		assertNotNull(extendedTaskSnaphotResult);
		assertNotNull(extendedTaskSnaphotResult.getSnapshot());
		assertNotNull(extendedTaskSnaphotResult.getMessages());
		// assertTrue(highmedTaskRequestSimpleFeasibilitySnapshot.getMessages().isEmpty());

		StructureDefinition extendedTaskSnapshot = extendedTaskSnaphotResult.getSnapshot();
		extendedTaskSnapshot.setSnapshot(
				new StructureDefinitionSnapshotComponent().setElement(extendedTaskSnapshot.getSnapshot().getElement()));

		logger.info("Snapshot generated for StructureDefinition with url {}\n{}",
				baseTaskSnapshotResult.getSnapshot().getUrl(),
				context.newXmlParser().setPrettyPrint(true).encodeResourceToString(extendedTaskSnapshot));
	}
}
