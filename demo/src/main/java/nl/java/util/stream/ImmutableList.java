package nl.java.util.stream;

import java.util.ArrayList;

class Animal{}
class Horse extends Animal{}
class Dog extends Animal{}
class ArabianHorse extends Horse{}

public class  ImmutableList<T> extends ArrayList <T>{
	
	private static final long serialVersionUID = 1L;

	public ImmutableList(){
		super();
	}
	
	// would like to express/enforce that <U super T> or <T extends U>
	public <U> ImmutableList<U> append(U element)
	{
		ImmutableList<U> l= new ImmutableList<U>();
		stream().forEach( t-> l.add( (U) t)); // not typesafe
		l.add(element);	
		return l;
	}
	
	public static void main(String[] args) {
		ArabianHorse arabian= new ArabianHorse();
		Dog dog= new Dog();
		ImmutableList<Horse> horses= new ImmutableList<>();
		
		horses= horses.append( arabian);
		// horses.add( dog); --> not allowed
		ImmutableList<Animal> animals= horses.append( dog);// a new list is created
		animals.stream( ).forEach( System.out::println); 
		
		ImmutableList<Dog> dogs= horses.append( dog);// misuse of the unsafe operation
		dogs.stream( ).forEach( System.out::println); // collapse!
		
		
	}

}
