import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ProductStockTest {
    private ProductStock stock;
    private Product product;

    @Before
    public void setUp () {
        stock = new Instock (new ArrayList<> ());
        product = createProduct ();
    }

    @Test
    public void testCountShouldReturnCorrectValue () {
        assertEquals (0,stock.getCount ());
        stock.add (product);
        assertEquals (1,stock.getCount ());
    }

    @Test
    public void testShouldReturnCorrectBooleanForAllCases () {

        //when enmpty
        assertFalse (stock.contains (product));
        //when added current product
        stock.add (product);
        assertTrue (stock.contains (product));
        //when loaded but doesnt contain the product
        assertFalse (stock.contains (new Product ("Not_Present_Label",3,12)));
    }

    @Test
    public void testAddProductShouldStoreCorrectProduct () {

        stock.add (product);
        assertTrue (stock.contains (product));
    }

    @Test
    public void testFindByIndexShouldReturnCorrectIfFirstItemInStock () {
        stock.add (product);
        Product foundProduct = stock.find (0);
        assertNotNull (foundProduct);
        assertEquals (product.getLabel (),foundProduct.getLabel ());
    }

    @Test
    public void testFindByIndexShouldReturnCorrectIfLastItemInStock () {
        fillProductsArrayInStock (5);
        stock.add (product);
        Product foundProduct = stock.find (stock.getCount () - 1);
        assertNotNull (foundProduct);
        assertEquals (product.getLabel (),foundProduct.getLabel ());
    }

    @Test
    public void testFindByIndexShouldReturnCorrectIfMiddleItemInStock () {
        fillProductsArrayInStock (5);
        stock.add (product);
        fillProductsArrayInStock (5);
        Product foundProduct = stock.find (5);
        assertNotNull (foundProduct);
        assertEquals (product.getLabel (),foundProduct.getLabel ());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testFindByIndexWithNegativeIndex () {
        stock.find (-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testFindByIndexWithEmptyStock () {
        stock.find (0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testFindByIndexWithIndexEqualToSize () {
        fillProductsArrayInStock (5);
        stock.find (stock.getCount ());
    }

    @Test
    public void testChangeShouldSetNewQuantityToCorrectProduct () {
        stock.add (product);
        int newQuantity = product.getQuantity () + 10;
        stock.changeQuantity (product.getLabel (),newQuantity);
        Product foundProduct = stock.find (0);
        assertEquals (newQuantity,product.getQuantity ());
        assertNotNull (foundProduct);
        assertEquals (newQuantity,foundProduct.getQuantity ());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeQuantityForItemNotPresent () {
        stock.changeQuantity (product.getLabel (),product.getQuantity () + 12);
    }

    @Test
    public void testFindByLabelShouldReturnCorrectItem () {
        stock.add (product);
        Product byLabel = stock.findByLabel (product.getLabel ());
        assertNotNull (byLabel);
        assertEquals (product.getLabel (),byLabel.getLabel ());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByLabelWithNoSuchProductFound () {
        stock.findByLabel ("Missing_Label_Here");
    }

    @Test
    public void testFindFirstByAlphabeticalOrderShouldReturnCorrectNumberOfProductsAndOrderedAlphabetically () {
        fillProductsArrayInStock (10);
        Iterable<Product> foundProducts = stock.findFirstByAlphabeticalOrder (8);
        assertNotNull (foundProducts);
        List<Product> returnedProducts = createListFromIterable (foundProducts);
        assertEquals (8,returnedProducts.size ());
        Set<String> expectedLabels = returnedProducts.stream ()
                .map (Product::getLabel)
                .collect (Collectors.toCollection (TreeSet::new));
        int index = 0;
        for (String label : expectedLabels) {
            assertEquals (label,returnedProducts.get (index++).getLabel ());
        }
    }

    @Test
    public void testFindFirstByAlphabeticalOrderShouldReturnEmptyCollection () {
        fillProductsArrayInStock (10);
        Iterable<Product> products = stock.findFirstByAlphabeticalOrder (11);
        assertNotNull (products);
        List<Product> list = createListFromIterable (products);
        assertTrue (list.isEmpty ());
    }

    @Test
    public void testFindAllInPriceRangeShouldReturnCorrectItemsWithCorrectOrder () {
        Product[] products = new Product[5];
        for (int i = 0; i < products.length; i++) {
            products[i] = new Product ("A_product_" + i,1 + i,1 + i);
        }
        for (Product product1 : products) {
            stock.add (product1);
        }
        Iterable<Product> allInRange = stock.findAllInRange (1,4);
        assertNotNull (allInRange);
        List<Product> listOfIterableProducts = createListFromIterable (allInRange);
        assertEquals (3,listOfIterableProducts.size ());
        assertEquals ("A_product_3",listOfIterableProducts.get (0).getLabel ());
        assertEquals (4,listOfIterableProducts.get (0).getPrice (),0);
        assertEquals ("A_product_2",listOfIterableProducts.get (1).getLabel ());
        assertEquals (3,listOfIterableProducts.get (1).getPrice (),0);
        assertEquals ("A_product_1",listOfIterableProducts.get (2).getLabel ());
        assertEquals (2,listOfIterableProducts.get (2).getPrice (),0);
    }

    @Test
    public void testFindAllInPriceRangeShouldReturnEmptyCollectionWhenNoneInRange () {
        Product[] products = new Product[5];
        for (int i = 0; i < products.length; i++) {
            products[i] = new Product ("A_product_" + i,1 + i,1 + i);
        }
        for (Product product1 : products) {
            stock.add (product1);
        }
        Iterable<Product> allInRange = stock.findAllInRange (6,8);
        assertNotNull (allInRange);
        List<Product> listOfIterableProducts = createListFromIterable (allInRange);
        assertTrue (listOfIterableProducts.isEmpty ());
    }

    @Test
    public void testFindAllByPriceReturnsCorrectPricedItemsOnly () {
        Product[] products = new Product[10];
        for (int i = 0; i < products.length; i += 2) {
            products[i] = new Product ("A_product_" + i,1 + i,1 + i);
            products[i + 1] = new Product ("A_product_" + i,2,1 + i);

        }
        for (Product product1 : products) {
            stock.add (product1);
        }
        Iterable<Product> allByPrice = stock.findAllByPrice (2);
        assertNotNull (allByPrice);
        List<Product> listFromIterable = createListFromIterable (allByPrice);
        assertEquals (5,listFromIterable.size ());
        for (Product product1 : listFromIterable) {
            assertEquals (2,product1.getPrice (),0);
        }
    }

    @Test
    public void testFindAllByPriceReturnsEmptyCollectionWhenNoItemsWithSearchedPrice () {
        fillProductsArrayInStock (5);
        Iterable<Product> allByPrice = stock.findAllByPrice (10);
        assertNotNull (allByPrice);
        List<Product> listFromIterable = createListFromIterable (allByPrice);
        assertTrue (listFromIterable.isEmpty ());
    }

    @Test
    public void testFindFirstMostExpensiveProductsReturnCorrectPricedProducts () {
        Product[] products = new Product[10];
        for (int i = 0; i < products.length; i++) {
            products[i] = new Product ("A_product_" + i,1 + i,1 + i);
        }
        for (Product product1 : products) {
            stock.add (product1);
        }
        Iterable<Product> firstMostExpensiveProducts = stock.findFirstMostExpensiveProducts (5);
        assertNotNull (firstMostExpensiveProducts);
        List<Product> listFromIterable = createListFromIterable (firstMostExpensiveProducts);
        assertEquals (10,listFromIterable.get (0).getPrice (),0);
        assertEquals (9,listFromIterable.get (1).getPrice (),0);
        assertEquals (8,listFromIterable.get (2).getPrice (),0);
        assertEquals (7,listFromIterable.get (3).getPrice (),0);
        assertEquals (6,listFromIterable.get (4).getPrice (),0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFirstMostExpensiveProductsThrowsExceptionWhenNotEnoughItemsInStock () {
        fillProductsArrayInStock (5);
        stock.findFirstMostExpensiveProducts (6);
    }

    @Test
    public void testFindAllByQuantityReturnsCorrectProducts () {
        Product[] products = new Product[10];
        for (int i = 0; i < 5; i++) {
            products[i] = new Product ("A_product_" + i,1 + i,1 + i);
        }

        for (int i = 5; i < 10; i++) {
            products[i] = new Product ("A_product_" + i,5,12);
        }
        for (Product product1 : products) {
            stock.add (product1);
        }
        Iterable<Product> allByQuantity = stock.findAllByQuantity (12);
        assertNotNull (allByQuantity);
        List<Product> listFromIterable = createListFromIterable (allByQuantity);
        assertEquals (5,listFromIterable.size ());
        assertEquals (12,listFromIterable.get (0).getQuantity ());
        assertEquals (12,listFromIterable.get (1).getQuantity ());
        assertEquals (12,listFromIterable.get (2).getQuantity ());
        assertEquals (12,listFromIterable.get (3).getQuantity ());
        assertEquals (12,listFromIterable.get (4).getQuantity ());
    }

    @Test
    public void testFindAllByQuantityReturnsEmptyCollectionWhenNoSuchItems () {
        fillProductsArrayInStock (5);
        Iterable<Product> allByQuantity = stock.findAllByQuantity (8);
        assertNotNull (allByQuantity);
        List<Product> listFromIterable = createListFromIterable (allByQuantity);
        assertTrue (listFromIterable.isEmpty ());
    }

    @Test
    public void testGetIterableReturnsAllProducts () {
        Product[] products = new Product[10];
        for (int i = 0; i < products.length; i++) {
            products[i] = new Product ("A_product_" + i,1 + i,1 + i);
        }
        for (Product product1 : products) {
            stock.add (product1);
        }
        Iterator<Product> iterable = stock.iterator ();
        assertNotNull (iterable);
        List<Product> listFromIterable = createListFromIterable (iterable);
        assertEquals (10,listFromIterable.size ());
    }

    @Test
    public void testGetIterableReturnsEmptyCollectionWhenNoProductsArePresent () {
        Iterator<Product> productIterable = stock.iterator ();
        assertNotNull (productIterable);
        List<Product> listFromIterable = createListFromIterable (productIterable);
        assertTrue (listFromIterable.isEmpty ());
    }

    // Helper methods for the tests//
    private <T> List<T> createListFromIterable (Iterable<T> foundProducts) {
        List<T> result = new ArrayList<> ();
        for (T product : foundProducts) {
            result.add (product);
        }
        return result;
    }

    private <T> List<T> createListFromIterable (Iterator<T> foundProducts) {
        List<T> result = new ArrayList<> ();
        while (foundProducts.hasNext ()) {
            result.add (foundProducts.next ());
        }
        return result;
    }

    private void fillProductsArrayInStock (int count) {
        Product[] productArray = createProductArray (count);
        for (Product product1 : productArray) {
            stock.add (product1);
        }
    }

    private Product createProduct () {
        return new Product ("Salam",3,15);
    }

    private Product[] createProductArray (int count) {
        Product[] products = new Product[count];
        for (int i = 0; i < products.length; i++) {
            products[i] = new Product ("Salam_" + i,3,15);

        }
        return products;
    }
}