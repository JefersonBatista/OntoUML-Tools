module Model

open world_structure[World]
open ontological_properties[World]
open util/relation
open util/ternary
open util/boolean

sig Object {}

sig Property {}

sig DataType {}

abstract sig World {
	exists: some Object+Property,
	Alive: set exists:>Object,
	Decease: set exists:>Object,
	Husband: set exists:>Object,
	Man: set exists:>Object,
	Marriage: set exists:>Property,
	Person: set exists:>Object,
	Wife: set exists:>Object,
	Woman: set exists:>Object,
	Mediation2: set Marriage one -> one Husband,
	Mediation3: set Marriage one -> one Wife
}{
	exists:>Object in Person
	exists:>Property in Marriage
}

fact additionalFacts {
	continuous_existence[exists]
	elements_existence[Object+Property,exists]
}

fact relatorConstraint {
	all w: World | all x: w.Marriage | # (x.(w.Mediation3)+x.(w.Mediation2)) >= 2
}

fact rigidity {
	rigidity[Marriage,Property,exists]
}

fact rigidity {
	rigidity[Man,Object,exists]
}

fact rigidity {
	rigidity[Person,Object,exists]
}

fact rigidity {
	rigidity[Woman,Object,exists]
}

fact antirigidity {
	antirigidity[Husband,Object,exists]
}

fact antirigidity {
	antirigidity[Wife,Object,exists]
}

fact antirigidity {
	antirigidity[Alive,Object,exists]
}

fact antirigidity {
	antirigidity[Decease,Object,exists]
}

fact generalization {
	Woman in Person
}

fact generalization {
	Man in Person
}

fact generalization {
	Alive in Person
}

fact generalization {
	Husband in Man
}

fact generalization {
	Wife in Woman
}

fact generalization {
	Decease in Person
}

fact generalizationSet {
	Person = Woman+Man
	disj[Man,Woman]
}

fact generalizationSet {
	Person = Alive+Decease
	disj[Decease,Alive]
}

fun visible : World -> univ {
	exists
}

fact associationProperties {
	immutable_target[Marriage,Mediation3]
	immutable_target[Marriage,Mediation2]
}

fun marriage [x: World.Husband,w: World] : set World.Marriage {
	(w.Mediation2).x
}

fun marriage [x: World.Husband] : set World.Marriage {
	(World.Mediation2).x
}

fun husband [x: World.Marriage,w: World] : set World.Husband {
	x.(w.Mediation2)
}

fun husband [x: World.Marriage] : set World.Husband {
	x.(World.Mediation2)
}

fun marriage1 [x: World.Wife,w: World] : set World.Marriage {
	(w.Mediation3).x
}

fun marriage1 [x: World.Wife] : set World.Marriage {
	(World.Mediation3).x
}

fun wife [x: World.Marriage,w: World] : set World.Wife {
	x.(w.Mediation3)
}

fun wife [x: World.Marriage] : set World.Wife {
	x.(World.Mediation3)
}

run smallSingleWorld { } for 10 but 1 World, 7 int
run smallMultipleWorlds { } for 10 but 3 World, 7 int
run mediumSingleWorld { } for 15 but 1 World, 7 int
run mediumMultipleWorlds { } for 10 but 3 World, 7 int




