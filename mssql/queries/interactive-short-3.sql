/* IS3. Friends of a Person
\set personId 17592186044461
 */
SELECT Person.personId, Person.firstName, person.lastName, k.creationDate
FROM Person_knows_Person k, Person
WHERE k.Person1Id = :personId
  AND k.Person2Id = Person.personId
ORDER BY k.creationDate DESC, Person.personId ASC
;