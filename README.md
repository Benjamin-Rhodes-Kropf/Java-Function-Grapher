# Java-Function-Grapher

![Annotation 2022-08-10 132620](https://user-images.githubusercontent.com/85074410/183977503-27de9bf0-de13-45b4-9f40-88f1eeb8ace9.jpg)

## Features
- zoom towards mouse pointer using scroll wheel
- transalte using WASD
- Input n dimesional power equations using java scanner
- Overide equation reader to graph custom functions such as sin or cos with ease
- Seperation of math and graphics
- graphs min and max points

## How To Enter Your Own Equations
### first go to the manager file and set 'userInputOverride' to true 
```javascript
private boolean userInputOverride = true; //like this
```
### then go to the 'rawEquation' function and enter your function as shown below:

```javascript
public double rawEquation(double input){
		double output = 0;
		if(userInputOverride){
			//type your equation here as output = 'input'
			//here is an examples:
			output = Math.sin(input/10)*10; //'y=sin(x/10)*10'
		}else{
			for(int x = 0; x < equationVariables.size(); x++) {
				output += equationVariables.get(x) * Math.pow(input,x);
			}
		}
		return (output);
	}
```


## Problems
- min and max point no longer have worldspace accuracy
- if you zoom to far out everything breaks

## Areas for Inprovment
- graphspace to worldspace should be done with a static well defined function
- add ability to graph more than just functions such as circles
- display area under curve rather than print it out
- display function rather than print it out
- graph multiple functions
- make more intuitive input
- re-impliment linear and logistic regression
- zooming twords mouse should be done with matrix math
- grid lines should be infinitly zoomable in and out (codded algorthimicaly rather than hard coded)





